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
package org.apache.asn1new.ldap.codec;

import java.nio.ByteBuffer;

import javax.naming.NamingException;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.Asn1Decoder;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ldap.pojo.ExtendedRequest;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.util.StringUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test the ExtendedRequest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedRequestTest extends TestCase {
    /**
     * Test the decoding of a full ExtendedRequest
     */
    public void testDecodeExtendedRequestSuccess() throws NamingException
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x16 );
        
        stream.put(
            new byte[]
            {
                0x30, 0x14, 		// LDAPMessage ::= SEQUENCE {
				0x02, 0x01, 0x01, 	//     messageID MessageID
				            		//     CHOICE { ..., extendedReq     ExtendedRequest, ...
				0x77, 0x0F,         // ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
				                    //     requestName      [0] LDAPOID,
				(byte)0x80, 0x06, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x02,
				                    //     requestValue     [1] OCTET STRING OPTIONAL }
				(byte)0x81, 0x05, 'v', 'a', 'l', 'u', 'e'
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the ExtendedRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        // Check the decoded ExtendedRequest PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        ExtendedRequest extendedRequest      = message.getExtendedRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "1.3.6.1.5.5.2", extendedRequest.getRequestName() );
        Assert.assertEquals( "value", extendedRequest.getRequestValue().toString() );
        
        // Check the length
        Assert.assertEquals(0x16, message.computeLength());

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a name only ExtendedRequest
     */
    public void testDecodeExtendedRequestName() throws NamingException
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x0F );
        
        stream.put(
            new byte[]
            {
                0x30, 0x0D, 		// LDAPMessage ::= SEQUENCE {
				0x02, 0x01, 0x01, 	//     messageID MessageID
				            		//     CHOICE { ..., extendedReq     ExtendedRequest, ...
				0x77, 0x08,         // ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
				                    //     requestName      [0] LDAPOID,
				(byte)0x80, 0x06, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x02,
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the ExtendedRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        // Check the decoded ExtendedRequest PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        ExtendedRequest extendedRequest      = message.getExtendedRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "1.3.6.1.5.5.2", extendedRequest.getRequestName() );
        
        // Check the length
        Assert.assertEquals(0x0F, message.computeLength());

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }
}
