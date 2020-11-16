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
package org.neo4j.server.plugins;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.server.rest.repr.formats.NullFormat;
import org.neo4j.test.TestGraphDatabaseFactory;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DefaultPluginManagerTest
{
    private static PluginManager manager;
    private static GraphDatabaseAPI graphDb;

    @BeforeClass
    public static void loadExtensionManager()
    {
        graphDb = (GraphDatabaseAPI) new TestGraphDatabaseFactory().newImpermanentDatabase();
        manager = new DefaultPluginManager( NullLogProvider.getInstance() );
    }

    @AfterClass
    public static void destroyExtensionManager()
    {
        manager = null;
        if ( graphDb != null )
        {
            graphDb.shutdown();
        }
        graphDb = null;
    }

    @Test
    public void canGetUrisForNode()
    {
        Map<String, List<String>> extensions = manager.getExensionsFor( GraphDatabaseService.class );
        List<String> methods = extensions.get( FunctionalTestPlugin.class.getSimpleName() );
        assertNotNull( methods );
        assertThat( methods, hasItem( FunctionalTestPlugin.CREATE_NODE ) );
    }

    @Test
    public void canInvokeExtension() throws Exception
    {
        manager.invoke( graphDb, FunctionalTestPlugin.class.getSimpleName(), GraphDatabaseService.class,
                FunctionalTestPlugin.CREATE_NODE, graphDb,
                new NullFormat( null, (MediaType[]) null ).readParameterList( "" ) );
    }
}
