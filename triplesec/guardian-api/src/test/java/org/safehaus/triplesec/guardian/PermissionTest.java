/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.guardian;


/**
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public class PermissionTest extends AbstractEntityTest
{

    protected Object newInstanceA1()
    {
        return new StringPermission("perm1" );
    }


    protected Object newInstanceA2()
    {
        return new StringPermission("perm1" );
    }


    protected Object newInstanceB1()
    {
        return new StringPermission("perm2" );
    }


    protected Object newInstanceB2()
    {
        return new StringPermission("perm2" );
    }


    public void testInstantiation()
    {
        try
        {
            new StringPermission(null );
            fail( "Exception is not thrown." );
        }
        catch ( NullPointerException e )
        {
            // OK
        }
//        try
//        {
//            new StringPermission("test" );
//            fail( "Exception is not thrown." );
//        }
//        catch ( NullPointerException e )
//        {
//            // OK
//        }
//        try
//        {
//            new StringPermission("" );
//            fail( "Exception is not thrown." );
//        }
//        catch ( IllegalArgumentException e )
//        {
//            // OK
//        }
//        try
//        {
//            new StringPermission("test" );
//            fail( "Exception is not thrown." );
//        }
//        catch ( IllegalArgumentException e )
//        {
//            // OK
//        }
    }


    public void testPropeties()
    {
        StringPermission p = new StringPermission("b");
        assertEquals( "b", p.getName() );
    }


    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( PermissionTest.class );
    }
}
