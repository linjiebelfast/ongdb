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
package org.neo4j.kernel.impl.enterprise.lock.forseti;

import java.time.Clock;

import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.locking.LockingCompatibilityTestSuite;
import org.neo4j.kernel.impl.locking.Locks;
import org.neo4j.kernel.impl.locking.ResourceTypes;
import org.neo4j.test.OtherThreadExecutor.WaitDetails;

public class ForsetiLocksTest extends LockingCompatibilityTestSuite
{
    @Override
    protected Locks createLockManager( Config config, Clock clock )
    {
        return new ForsetiLockManager( config, clock, ResourceTypes.values() );
    }

    @Override
    protected boolean isAwaitingLockAcquisition( WaitDetails details )
    {
        return details.isAt( ForsetiClient.class, "applyWaitStrategy" );
    }
}
