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
package org.neo4j.causalclustering.catchup.storecopy;

import java.io.File;
import java.io.IOException;

import org.neo4j.causalclustering.catchup.tx.FileCopyMonitor;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.kernel.monitoring.Monitors;

public class StreamToDiskProvider implements StoreFileStreamProvider
{
    private final File storeDir;
    private final FileSystemAbstraction fs;
    private final FileCopyMonitor fileCopyMonitor;

    StreamToDiskProvider( File storeDir, FileSystemAbstraction fs, Monitors monitors )
    {
        this.storeDir = storeDir;
        this.fs = fs;
        this.fileCopyMonitor = monitors.newMonitor( FileCopyMonitor.class );
    }

    @Override
    public StoreFileStream acquire( String destination, int requiredAlignment ) throws IOException
    {
        File fileName = new File( storeDir, destination );
        fs.mkdirs( fileName.getParentFile() );
        fileCopyMonitor.copyFile( fileName );
        return StreamToDisk.fromFile( fs, fileName );
    }
}
