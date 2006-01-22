/*
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
package org.apache.ldap.common.codec.extended.operations;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.asn1.Asn1Object;
import org.apache.asn1.ber.tlv.Length;
import org.apache.asn1.ber.tlv.UniversalTag;
import org.apache.asn1.ber.tlv.Value;
import org.apache.asn1.codec.EncoderException;
import org.apache.ldap.common.codec.util.LdapURL;


/**
 * An extended operation to proceed a graceful disconnect
 * 
 * <pre>
 *  GracefulDisconnect ::= SEQUENCE 
 *  {
 *      timeOffline INTEGER (0..720) DEFAULT 0,
 *      delay [0] INTEGER (0..86400) DEFAULT 0,
 *      replicatedContexts Referral OPTIONAL
 *  }
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class GracefulDisconnect extends Asn1Object
{
    private int timeOffline;
    private int delay;
    private List replicatedContexts;
    private transient int gracefulDisconnectSequenceLength;
    private transient int replicatedContextsLength;
    
    public GracefulDisconnect( int timeOffline, int delay )
    {
        this.timeOffline = timeOffline;
        this.delay = delay;
        this.replicatedContexts = new ArrayList(2);
    }

    public GracefulDisconnect()
    {
        this.timeOffline = 0;
        this.delay = 0;
        this.replicatedContexts = new ArrayList(2);
    }

    public int getDelay() 
    {
        return delay;
    }


    public void setDelay( int delay ) 
    {
        this.delay = delay;
    }


    public int getTimeOffline() 
    {
        return timeOffline;
    }


    public void setTimeOffline( int timeOffline ) 
    {
        this.timeOffline = timeOffline;
    }
    
    public List getReplicatedContexts() 
    {
        return replicatedContexts;
    }

    public void setReplicatedContexts( List replicatedContexts ) 
    {
        this.replicatedContexts = replicatedContexts;
    }

    public void addReplicatedContexts( LdapURL replicatedContext ) 
    {
        replicatedContexts.add( replicatedContext );
    }

    /**
     * Compute the GracefulDisconnect length
     * 0x30 L1
     *  |
     *  +--> [ 0x02 0x0(1-4) [0..720] ]
     *  +--> [ 0x80 0x0(1-3) [0..86400] ]
     *  +--> [ 0x30 L2
     *          |
     *          +--> (0x04 L3 value) + 
     *  
     */
    public int computeLength()
    {
        gracefulDisconnectSequenceLength = 0;
        
        if ( timeOffline != 0 )
        {
            gracefulDisconnectSequenceLength += 1 + 1 + Length.getNbBytes( timeOffline );
        }

        if ( delay != 0 )
        {
            gracefulDisconnectSequenceLength += 1 + 1 + Length.getNbBytes( delay );
        }

        if ( replicatedContexts.size() > 0 )
        {
            replicatedContextsLength = 0;

            Iterator replicatedContextIterator = replicatedContexts.iterator();
            
            // We may have more than one reference.
            while (replicatedContextIterator.hasNext())
            {
                int ldapUrlLength = ((LdapURL)replicatedContextIterator.next()).getNbBytes();
                replicatedContextsLength += 1 + Length.getNbBytes( ldapUrlLength ) + ldapUrlLength;
            }
        }
        else
        {
            replicatedContextsLength = 1 + 1;
        }
        
        gracefulDisconnectSequenceLength += 1 + Length.getNbBytes( replicatedContextsLength ) + replicatedContextsLength; 

        return 1 + Length.getNbBytes( gracefulDisconnectSequenceLength ) + gracefulDisconnectSequenceLength;
    }
    
    /**
     * Encodes the gracefulDisconnect extended request.
     * 
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws EncoderException If anything goes wrong.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        // Allocate the bytes buffer.
        ByteBuffer bb = ByteBuffer.allocate( computeLength() );
        
        bb.put( UniversalTag.SEQUENCE_TAG );
        bb.put( Length.getBytes( gracefulDisconnectSequenceLength ) );

        if ( timeOffline != 0 )
        {
            Value.encode( bb, timeOffline );
        }
        
        if ( delay != 0 )
        {
            bb.put( (byte)GracefulDisconnectConstants.GRACEFUL_DISCONNECT_DELAY_TAG );
            bb.put( (byte)Length.getNbBytes( delay ) );
            bb.put( Value.getBytes( delay ) );
        }
        
        if ( replicatedContexts.size() != 0 )
        {
            bb.put( UniversalTag.SEQUENCE_TAG );
            bb.put( Length.getBytes( replicatedContextsLength ) );
            
            Iterator replicatedContextIterator = replicatedContexts.iterator();
            
            // We may have more than one reference.
            while (replicatedContextIterator.hasNext())
            {
                LdapURL url = (LdapURL)replicatedContextIterator.next();
                Value.encode( bb, url.getBytes() );
            }
        }
                 
        return bb;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( "    TimeOffline : " ).append( timeOffline ).append( '\n' );
        sb.append( "    Delay : ").append( delay ).append( '\n' );
        
        if ( replicatedContexts.size() != 0 )
        {
            Iterator replicatedContextIterator = replicatedContexts.iterator();
            sb.append( "    Replicated contexts :" );
            
            // We may have more than one reference.
            while (replicatedContextIterator.hasNext())
            {
                LdapURL url = (LdapURL)replicatedContextIterator.next();
                sb.append( "\n        " ).append(url.toString() );
            }
        }
        
        return sb.toString();
    }
}
