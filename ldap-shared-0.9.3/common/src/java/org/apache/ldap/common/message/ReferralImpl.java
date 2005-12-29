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


import java.util.*;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.AbstractLockable;


/**
 * Lockable Referral implementation.  For the time being this implementation
 * uses a String representation for LDAPURLs.  In the future an LdapUrl
 * interface with default implementations will be used once a parser for an
 * LdapUrl is created.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ReferralImpl
    extends AbstractLockable implements Referral
{
    static final long serialVersionUID = 2638820668325359096L;
    /** Sequence of LDAPUrls composing this Referral */
    private final HashSet urls = new HashSet();


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a non-root Lockable Referral implemenation dependant on a parent
     * Lockable
     *
     * @param parent the overriding parent Lockable.
     */
    public ReferralImpl( final Lockable parent )
    {
        super( parent, false );
    }


    // ------------------------------------------------------------------------
    // LdapResult Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets an unmodifiable set of alternative referral urls.
     *
     * @return the alternative url objects.
     */
    public Collection getLdapUrls()
    {
        return Collections.unmodifiableCollection( urls );
    }


    /**
     * Adds an LDAPv3 URL to this Referral.
     *
     * @param url the LDAPv3 URL to add
     */
    public void addLdapUrl( String url )
    {
        lockCheck( "Atempt to add alternative url to locked Referral!" );
        urls.add( url );
    }


    /**
     * Removes an LDAPv3 URL to this Referral.
     *
     * @param url the LDAPv3 URL to remove
     */
    public void removeLdapUrl( String url )
    {
        lockCheck( "Atempt to remove alternative url from locked Referral!" );
        urls.remove( url );
    }


    /**
     * Compares this Referral implementation to see if it is the same as
     * another.  The classes do not have to be the same implementation to
     * return true.  Both this and the compared Referral must have the same
     * entries exactly.  The order of Referral URLs does not matter.
     *
     * @param obj the object to compare this ReferralImpl to
     * @return true if both implementations contain exactly the same URLs
     */
    public boolean equals( Object obj )
    {
        // just in case for speed return true if obj is this object
        if ( obj == this )
        {
            return true;
        }

        if ( obj instanceof Referral )
        {
            Collection refs = ( ( Referral ) obj ).getLdapUrls();

            // if their sizes do not match they are not equal
            if ( refs.size() != urls.size() )
            {
                return false;
            }

            Iterator list = urls.iterator();
            while ( list.hasNext() )
            {
                // if one of our urls is not contained in the obj return false
                if ( ! refs.contains( list.next() ) )
                {
                    return false;
                }
            }

            // made it through the checks so we have a match
            return true;
        }

        return false;
    }
}
