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
package org.neo4j.causalclustering.core;

import org.junit.Rule;
import org.junit.Test;

import java.util.function.Predicate;

import org.neo4j.causalclustering.core.state.machines.id.FreeIdFilteredIdGeneratorFactory;
import org.neo4j.causalclustering.discovery.Cluster;
import org.neo4j.causalclustering.discovery.CoreClusterMember;
import org.neo4j.com.storecopy.StoreUtil;
import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.kernel.impl.index.IndexConfigStore;
import org.neo4j.kernel.impl.pagecache.PageCacheWarmer;
import org.neo4j.kernel.impl.storageengine.impl.recordstorage.id.BufferedIdController;
import org.neo4j.kernel.impl.storageengine.impl.recordstorage.id.IdController;
import org.neo4j.kernel.impl.store.id.IdGeneratorFactory;
import org.neo4j.kernel.impl.transaction.log.files.TransactionLogFiles;
import org.neo4j.test.causalclustering.ClusterRule;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EnterpriseCoreEditionModuleIT
{
    @Rule
    public ClusterRule clusterRule = new ClusterRule();

    @Test
    public void createBufferedIdComponentsByDefault() throws Exception
    {
        Cluster<?> cluster = clusterRule.startCluster();
        CoreClusterMember leader = cluster.awaitLeader();
        DependencyResolver dependencyResolver = leader.database().getDependencyResolver();

        IdController idController = dependencyResolver.resolveDependency( IdController.class );
        IdGeneratorFactory idGeneratorFactory = dependencyResolver.resolveDependency( IdGeneratorFactory.class );

        assertThat( idController, instanceOf( BufferedIdController.class ) );
        assertThat( idGeneratorFactory, instanceOf( FreeIdFilteredIdGeneratorFactory.class ) );
    }

    @Test
    public void fileWatcherFileNameFilter()
    {
        DatabaseLayout layout = clusterRule.testDirectory().databaseLayout();
        Predicate<String> filter = EnterpriseCoreEditionModule.fileWatcherFileNameFilter();
        String metadataStoreName = layout.metadataStore().getName();
        assertFalse( filter.test( metadataStoreName ) );
        assertFalse( filter.test( layout.nodeStore().getName() ) );
        assertTrue( filter.test( TransactionLogFiles.DEFAULT_NAME + ".1" ) );
        assertTrue( filter.test( IndexConfigStore.INDEX_DB_FILE_NAME + ".any" ) );
        assertTrue( filter.test( StoreUtil.TEMP_COPY_DIRECTORY_NAME ) );
        assertTrue( filter.test( metadataStoreName + PageCacheWarmer.SUFFIX_CACHEPROF ) );
    }
}
