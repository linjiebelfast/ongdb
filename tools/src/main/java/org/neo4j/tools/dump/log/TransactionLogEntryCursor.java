/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of ONgDB Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) as found
 * in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 */
package org.neo4j.tools.dump.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.cursor.IOCursor;
import org.neo4j.kernel.impl.transaction.log.entry.LogEntry;

import static org.neo4j.kernel.impl.transaction.log.entry.LogEntryByteCodes.CHECK_POINT;
import static org.neo4j.kernel.impl.transaction.log.entry.LogEntryByteCodes.TX_COMMIT;

/**
 * Groups {@link LogEntry} instances transaction by transaction
 */
public class TransactionLogEntryCursor implements IOCursor<LogEntry[]>
{
    private final IOCursor<LogEntry> delegate;
    private final List<LogEntry> transaction = new ArrayList<>();

    public TransactionLogEntryCursor( IOCursor<LogEntry> delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public LogEntry[] get()
    {
        return transaction.toArray( new LogEntry[transaction.size()] );
    }

    @Override
    public boolean next() throws IOException
    {
        transaction.clear();
        LogEntry entry;
        while ( delegate.next() )
        {
            entry = delegate.get();
            transaction.add( entry );
            if ( isBreakPoint( entry ) )
            {
                return true;
            }
        }
        return !transaction.isEmpty();
    }

    private static boolean isBreakPoint( LogEntry entry )
    {
        byte type = entry.getType();
        return type == TX_COMMIT || type == CHECK_POINT;
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }
}
