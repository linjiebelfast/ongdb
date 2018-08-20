/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
package org.neo4j.index.internal.gbptree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CrashGenerationCleanerConfigTest
{
    @Test
    public void everyThreadShouldHaveAtLeastOneBatchToWorkOn()
    {
        long pagesToClean = 1;
        long multiplier = 5;
        int threads = 0;
        int prevThreads;
        long batchSize;
        do
        {
            prevThreads = threads;
            threads = CrashGenerationCleaner.threads( pagesToClean );
            batchSize = CrashGenerationCleaner.batchSize( pagesToClean, threads );
            assertTrue( "at least one batch per thread", (pagesToClean + batchSize) / batchSize >= threads );
            pagesToClean *= multiplier;
        }
        while ( threads != prevThreads || batchSize < CrashGenerationCleaner.MAX_BATCH_SIZE );
        long aLotMorePages = 100 * pagesToClean;
        assertEquals( threads, CrashGenerationCleaner.threads( aLotMorePages ) );
        assertEquals( batchSize, CrashGenerationCleaner.batchSize( aLotMorePages, threads ) );
    }
}