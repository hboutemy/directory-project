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
package org.apache.ldap.common.berlib.asn1.encoder.search;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.LockableAttributesImpl;
import org.apache.ldap.common.message.SearchResponseEntryImpl;
import org.apache.ldap.common.message.LockableAttributeImpl;


/**
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SearchResponseEntryEncoderTest extends AbstractEncoderTestCase
{
    public void testEncode()
    {
        // Construct the Search response
        SearchResponseEntryImpl response = new SearchResponseEntryImpl( 45 );
        response.setObjectName( "dc=example,dc=com" );
        LockableAttributesImpl attrs = new LockableAttributesImpl( response );
        LockableAttributeImpl objectClass = new LockableAttributeImpl( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "dcObject" );
        attrs.put( objectClass );
        attrs.put( "dc", "example.com" );
        response.setAttributes( attrs );

        byte[] expected = new byte[] {0x30, 0x4F, 0x02, 0x01, 0x2D, 0x64, 0x4A, 0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x30, 0x35, 0x30, 0x1E, 0x04, 0x0B, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x31, 0x0F, 0x04, 0x08, 0x64, 0x63, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x04, 0x03, 0x74, 0x6F, 0x70, 0x30, 0x13, 0x04, 0x02, 0x64, 0x63, 0x31, 0x0D, 0x04, 0x0B, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = SearchResponseEntryEncoder.INSTANCE.encode( response );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }

}
