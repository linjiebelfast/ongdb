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
package org.neo4j.server.security.enterprise.auth;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;

import org.neo4j.internal.kernel.api.security.AuthenticationResult;

public class ShiroSubject extends DelegatingSubject
{
    private AuthenticationResult authenticationResult;
    private ShiroAuthenticationInfo authenticationInfo;

    public ShiroSubject( SecurityManager securityManager, AuthenticationResult authenticationResult )
    {
        super( securityManager );
        this.authenticationResult = authenticationResult;
    }

    public ShiroSubject( PrincipalCollection principals, boolean authenticated, String host, Session session,
            boolean sessionCreationEnabled, SecurityManager securityManager, AuthenticationResult authenticationResult,
            ShiroAuthenticationInfo authenticationInfo )
    {
        super( principals, authenticated, host, session, sessionCreationEnabled, securityManager );
        this.authenticationResult = authenticationResult;
        this.authenticationInfo = authenticationInfo;
    }

    public AuthenticationResult getAuthenticationResult()
    {
        return authenticationResult;
    }

    void setAuthenticationResult( AuthenticationResult authenticationResult )
    {
        this.authenticationResult = authenticationResult;
    }

    public ShiroAuthenticationInfo getAuthenticationInfo()
    {
        return authenticationInfo;
    }

    public void clearAuthenticationInfo()
    {
        this.authenticationInfo = null;
    }
}
