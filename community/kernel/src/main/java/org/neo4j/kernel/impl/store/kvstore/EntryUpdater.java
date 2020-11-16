/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
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
package org.neo4j.kernel.impl.store.kvstore;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

public abstract class EntryUpdater<Key> implements AutoCloseable
{
    private final Lock lock;
    private Thread thread;

    EntryUpdater( Lock lock )
    {
        this.lock = lock;
        if ( lock != null )
        {
            this.thread = Thread.currentThread();
            lock.lock();
        }
    }

    public abstract void apply( Key key, ValueUpdate update ) throws IOException;

    @Override
    public void close()
    {
        if ( thread != null )
        {
            if ( thread != Thread.currentThread() )
            {
                throw new IllegalStateException( "Closing on different thread." );
            }
            lock.unlock();
            thread = null;
        }
    }

    protected void ensureOpenOnSameThread()
    {
        if ( thread != Thread.currentThread() )
        {
            throw new IllegalStateException( "The updater is not available." );
        }
    }

    protected void ensureOpen()
    {
        if ( thread == null )
        {
            throw new IllegalStateException( "The updater is not available." );
        }
    }

    @SuppressWarnings( "unchecked" )
    static <Key> EntryUpdater<Key> noUpdates()
    {
        return NO_UPDATES;
    }

    private static final EntryUpdater NO_UPDATES = new EntryUpdater( null )
    {
        @Override
        public void apply( Object o, ValueUpdate update )
        {
        }

        @Override
        public void close()
        {
        }
    };
}
