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
package org.neo4j.causalclustering.core.consensus;

import org.neo4j.causalclustering.core.consensus.roles.AppendEntriesRequestBuilder;
import org.neo4j.causalclustering.core.consensus.roles.AppendEntriesResponseBuilder;
import org.neo4j.causalclustering.core.consensus.vote.PreVoteRequestBuilder;
import org.neo4j.causalclustering.core.consensus.vote.PreVoteResponseBuilder;
import org.neo4j.causalclustering.core.consensus.vote.VoteRequestBuilder;
import org.neo4j.causalclustering.core.consensus.vote.VoteResponseBuilder;

public class TestMessageBuilders
{
    private TestMessageBuilders()
    {
    }

    public static AppendEntriesRequestBuilder appendEntriesRequest()
    {
        return new AppendEntriesRequestBuilder();
    }

    public static AppendEntriesResponseBuilder appendEntriesResponse()
    {
        return new AppendEntriesResponseBuilder();
    }

    public static HeartbeatBuilder heartbeat()
    {
        return new HeartbeatBuilder();
    }

    public static VoteRequestBuilder voteRequest()
    {
        return new VoteRequestBuilder();
    }

    public static PreVoteRequestBuilder preVoteRequest()
    {
        return new PreVoteRequestBuilder();
    }

    public static VoteResponseBuilder voteResponse()
    {
        return new VoteResponseBuilder();
    }

    public static PreVoteResponseBuilder preVoteResponse()
    {
        return new PreVoteResponseBuilder();
    }
}
