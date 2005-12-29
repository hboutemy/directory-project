/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.asn1new.ldap.pojo;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.tlv.Length;
import org.apache.asn1new.ldap.codec.LdapConstants;

/**
 * An ModifyResponse Message. Its syntax is :
 *   ModifyResponse ::= [APPLICATION 7] LDAPResult
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyResponse extends LdapResponse
{
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ModifyResponse object.
     */
    public ModifyResponse()
    {
        super( );
    }

    /**
     * Get the message type
     *
     * @return Returns the type.
     */
    public int getMessageType()
    {
        return LdapConstants.MODIFY_RESPONSE;
    }

    /**
     * Compute the ModifyResponse length
     * 
     * ModifyResponse :
     * 
     * 0x67 L1
     *  |
     *  +--> LdapResult
     * 
     * L1 = Length(LdapResult)
     * 
     * Length(ModifyResponse) = Length(0x67) + Length(L1) + L1
     */
    public int computeLength()
    {
        int ldapResponseLength = super.computeLength();
        
        return 1 + Length.getNbBytes( ldapResponseLength ) + ldapResponseLength;
    }

    /**
     * Encode the ModifyResponse message to a PDU.
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer )  throws EncoderException
    {
        if (buffer == null)
        {
            throw new EncoderException("Cannot put a PDU in a null buffer !");
        }
        
        try
        {
            // The tag
            buffer.put( LdapConstants.MODIFY_RESPONSE_TAG );
            buffer.put( Length.getBytes( getLdapResponseLength() ) );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException("The PDU buffer size is too small !"); 
        }
        
        // The ldapResult
        return super.encode( buffer);
    }

    /**
     * Get a String representation of a ModifyResponse
     *
     * @return A ModifyResponse String 
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( "    Modify Response\n" );
        sb.append( super.toString() );

        return sb.toString();
    }
}
