/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
package org.neo4j.unsafe.impl.batchimport;

import org.neo4j.annotations.service.ServiceProvider;
import org.neo4j.configuration.Config;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.impl.store.format.RecordFormats;
import org.neo4j.logging.internal.LogService;
import org.neo4j.scheduler.JobScheduler;
import org.neo4j.unsafe.impl.batchimport.input.Collector;
import org.neo4j.unsafe.impl.batchimport.staging.ExecutionMonitor;

@ServiceProvider
public class StandardBatchImporterFactory extends BatchImporterFactory
{
    public StandardBatchImporterFactory()
    {
        super( 1 );
    }

    @Override
    public String getName()
    {
        return "standard";
    }

    @Override
    public BatchImporter instantiate( DatabaseLayout directoryStructure, FileSystemAbstraction fileSystem, PageCache externalPageCache, Configuration config,
            LogService logService, ExecutionMonitor executionMonitor, AdditionalInitialIds additionalInitialIds, Config dbConfig,
            RecordFormats recordFormats, ImportLogic.Monitor monitor, JobScheduler scheduler, Collector badCollector )
    {
        return new ParallelBatchImporter( directoryStructure, fileSystem, externalPageCache, config, logService, executionMonitor,
                additionalInitialIds, dbConfig, recordFormats, monitor, scheduler, badCollector );
    }
}
