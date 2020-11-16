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
package org.neo4j.server.rest.management;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.neo4j.server.NeoServer;
import org.neo4j.server.rest.management.repr.ServerRootRepresentation;
import org.neo4j.server.rest.repr.OutputFormat;

@Path( "/" )
public class RootService
{
    private final NeoServer neoServer;

    public RootService( @Context NeoServer neoServer )
    {
        this.neoServer = neoServer;
    }

    @GET
    public Response getServiceDefinition( @Context UriInfo uriInfo, @Context OutputFormat output )
    {
        ServerRootRepresentation representation =
                new ServerRootRepresentation( uriInfo.getBaseUri(), neoServer.getServices() );

        return output.ok( representation );
    }
}
