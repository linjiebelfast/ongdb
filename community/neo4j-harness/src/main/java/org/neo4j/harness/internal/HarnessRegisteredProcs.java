/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of ONgDB.
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
package org.neo4j.harness.internal;

import java.util.LinkedList;
import java.util.List;

import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;

public class HarnessRegisteredProcs
{
    private final List<Class<?>> procs = new LinkedList<>();
    private final List<Class<?>> functions = new LinkedList<>();
    private final List<Class<?>> aggregationFunctions = new LinkedList<>();

    public void addProcedure( Class<?> procedureClass )
    {
        this.procs.add( procedureClass );
    }

    public void addFunction( Class<?> functionClass )
    {
        this.functions.add( functionClass );
    }

    public void addAggregationFunction( Class<?> functionClass )
    {
        this.aggregationFunctions.add( functionClass );
    }

    @SuppressWarnings( "deprecation" )
    public void applyTo( Procedures procedures ) throws KernelException
    {
        for ( Class<?> cls : procs )
        {
            procedures.registerProcedure( cls );
        }

        for ( Class<?> cls : functions )
        {
            procedures.registerFunction( cls );
        }

        for ( Class<?> cls : aggregationFunctions )
        {
            procedures.registerAggregationFunction( cls );
        }
    }
}
