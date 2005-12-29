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
package org.apache.ldap.common.message;


import junit.framework.TestCase;

import java.util.Collection;
import java.util.Collections;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.LockException;


/**
 * TestCase for the methods of the DeleteRequestImpl class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class DeleteRequestImplTest extends TestCase
{
    /**
     * Tests the same object referrence for equality.
     */
    public void testEqualsSameObj()
    {
        DeleteRequestImpl req = new DeleteRequestImpl( 5 );
        assertTrue( req.equals( req ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    public void testEqualsExactCopy()
    {
        DeleteRequestImpl req0 = new DeleteRequestImpl( 5 );
        req0.setName( "cn=admin,dc=example,dc=com" );

        DeleteRequestImpl req1 = new DeleteRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );

        assertTrue( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the IDs are different.
     */
    public void testNotEqualDiffId()
    {
        DeleteRequestImpl req0 = new DeleteRequestImpl( 7 );
        req0.setName( "cn=admin,dc=example,dc=com" );

        DeleteRequestImpl req1 = new DeleteRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the DN names are different.
     */
    public void testNotEqualDiffName()
    {
        DeleteRequestImpl req0 = new DeleteRequestImpl( 5 );
        req0.setName( "uid=akarasulu,dc=example,dc=com" );

        DeleteRequestImpl req1 = new DeleteRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Tests for equality even when another DeleteRequest implementation is used.
     */
    public void testEqualsDiffImpl()
    {
        DeleteRequest req0 = new DeleteRequest()
        {
            public String getName()
            {
                return null;
            }

            public void setName( String a_name )
            {
            }

            public MessageTypeEnum getResponseType()
            {
                return MessageTypeEnum.DELRESPONSE;
            }

            public boolean hasResponse()
            {
                return true;
            }

            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.DELREQUEST;
            }

            public Collection getControls()
            {
                return Collections.EMPTY_LIST;
            }

            public void add( Control a_control ) throws MessageException
            {
            }

            public void remove( Control a_control ) throws MessageException
            {
            }

            public int getMessageId()
            {
                return 5;
            }

            public Object get( Object a_key )
            {
                return null;
            }

            public Object put( Object a_key, Object a_value )
            {
                return null;
            }

            public Lockable getParent()
            {
                return null;
            }

            public boolean isLocked()
            {
                return false;
            }

            public boolean getLocked()
            {
                return false;
            }

            public void setLocked( boolean a_isLocked ) throws LockException
            {
            }

            public boolean isUnlockable()
            {
                return false;
            }
        };

        DeleteRequestImpl req1 = new DeleteRequestImpl( 5 );
        assertTrue( req1.equals( req0 ) );
    }
}
