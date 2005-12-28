/*
 *   @(#) $Id$
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.mina.common.support;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoFilterChainBuilder;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;

/**
 * A base implementation of {@link IoAcceptor}.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public abstract class BaseIoAcceptor extends BaseIoSessionManager implements IoAcceptor
{
    /* TODO: DIRMINA-93
    private boolean disconnectClientsOnUnbind = true;
    */
    
    protected BaseIoAcceptor()
    {
    }
    
    public void bind( SocketAddress address, IoHandler handler ) throws IOException
    {
        this.bind( address, handler, IoFilterChainBuilder.NOOP );
    }

    /* TODO: DIRMINA-93
    public boolean isDisconnectClientsOnUnbind()
    {
        return disconnectClientsOnUnbind;
    }

    public void setDisconnectClientsOnUnbind( boolean disconnectClientsOnUnbind )
    {
        this.disconnectClientsOnUnbind = disconnectClientsOnUnbind;
    }
    */
    public IoSession newSession( SocketAddress remoteAddress, SocketAddress localAddress )
    {
        throw new UnsupportedOperationException();
    }
}
