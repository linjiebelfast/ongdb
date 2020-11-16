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
package org.neo4j.server.rest.transactional;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

import javax.ws.rs.ext.Provider;

import org.neo4j.server.database.Database;
import org.neo4j.server.database.InjectableProvider;

@Provider
public class TransactionFilter extends InjectableProvider<Void> implements ResourceMethodDispatchAdapter
{
    private Database database;

    public TransactionFilter( Database database )
    {
        super( Void.class );
        this.database = database;
    }

    @Override
    public Void getValue( HttpContext httpContext )
    {
        throw new IllegalStateException( "This _really_ should never happen" );
    }

    @Override
    public ResourceMethodDispatchProvider adapt( final ResourceMethodDispatchProvider resourceMethodDispatchProvider )
    {
        return abstractResourceMethod ->
        {
            final RequestDispatcher requestDispatcher = resourceMethodDispatchProvider.create(
                    abstractResourceMethod );

            return new TransactionalRequestDispatcher( database, requestDispatcher );
        };
    }
}
