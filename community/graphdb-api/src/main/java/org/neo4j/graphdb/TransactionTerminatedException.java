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
package org.neo4j.graphdb;

import org.neo4j.kernel.api.exceptions.Status;

/**
 * Signals that the transaction within which the failed operations ran
 * has been terminated with {@link Transaction#terminate()}.
 */
public class TransactionTerminatedException extends TransactionFailureException implements Status.HasStatus
{
    private final Status status;

    public TransactionTerminatedException( Status status )
    {
        this( status, "" );
    }

    protected TransactionTerminatedException( Status status, String additionalInfo )
    {
        super( "The transaction has been terminated. Retry your operation in a new transaction, " +
               "and you should see a successful result. " + status.code().description() + " " + additionalInfo );
        this.status = status;
    }

    @Override
    public Status status()
    {
        return status;
    }
}
