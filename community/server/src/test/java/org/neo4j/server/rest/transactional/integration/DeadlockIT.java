/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.rest.transactional.integration;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.rest.AbstractRestFunctionalTestBase;
import org.neo4j.test.OtherThreadExecutor;
import org.neo4j.test.rule.concurrent.OtherThreadRule;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.neo4j.kernel.api.exceptions.Status.Transaction.DeadlockDetected;
import static org.neo4j.test.server.HTTP.RawPayload.quotedJson;

public class DeadlockIT extends AbstractRestFunctionalTestBase
{
    private final HTTP.Builder http = HTTP.withBaseUri( server().baseUri() );

    @Rule
    public OtherThreadRule<Object> otherThread = new OtherThreadRule<>();

    private final CountDownLatch secondNodeLocked = new CountDownLatch( 1 );

    @Test
    public void shouldReturnCorrectStatusCodeOnDeadlock() throws Exception
    {
        // Given
        try ( Transaction tx = graphdb().beginTx() )
        {
            graphdb().createNode( Label.label( "First" ) );
            graphdb().createNode( Label.label( "Second" ) );
            tx.success();
        }

        // When I lock node:First
        HTTP.Response begin = http.POST( "db/data/transaction",
                quotedJson( "{ 'statements': [ { 'statement': 'MATCH (n:First) SET n.prop=1' } ] }" ));

        // and I lock node:Second, and wait for a lock on node:First in another transaction
        otherThread.execute( writeToFirstAndSecond() );

        // and I wait for those locks to be pending
        assertTrue( secondNodeLocked.await( 10, TimeUnit.SECONDS ) );
        Thread.sleep( 1000 );

        // and I then try and lock node:Second in the first transaction
        HTTP.Response deadlock = http.POST( begin.location(),
                quotedJson( "{ 'statements': [ { 'statement': 'MATCH (n:Second) SET n.prop=1' } ] }" ));

        // Then
        assertThat( deadlock.get( "errors" ).get( 0 ).get( "code" ).asText(),
                equalTo( DeadlockDetected.code().serialize() ) );
    }

    private OtherThreadExecutor.WorkerCommand<Object, Object> writeToFirstAndSecond()
    {
        return state ->
        {
            HTTP.Response post = http.POST( "db/data/transaction",
                    quotedJson( "{ 'statements': [ { 'statement': 'MATCH (n:Second) SET n.prop=1' } ] }" ) );
            secondNodeLocked.countDown();
            http.POST( post.location(),
                    quotedJson( "{ 'statements': [ { 'statement': 'MATCH (n:First) SET n.prop=1' } ] }" ) );
            return null;
        };
    }
}
