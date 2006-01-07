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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.BranchNormalizedVisitor;


/**
 * Lockable SearchRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchRequestImpl extends AbstractAbandonableRequest implements SearchRequest
{
    static final long serialVersionUID = -5655881944020886218L;
    /** Search base distinguished name */
    private String baseDn;
    /** Search filter expression tree's root node */
    private ExprNode filter;
    /** Search scope enumeration value */
    private ScopeEnum scope;
    /** Types only return flag */
    private boolean typesOnly;
    /** Max size in entries to return */
    private int sizeLimit;
    /** Max seconds to wait for search to complete */
    private int timeLimit;
    /** Alias dereferencing mode enumeration value */
    private DerefAliasesEnum derefAliases;
    /** Attributes to return */
    private Collection attributes = new ArrayList();
    /** The final result containing SearchResponseDone response */
    private SearchResponseDone response;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a Lockable SearcRequest implementing object used to search the DIT.
     *
     * @param id the sequential message identifier
     */
    public SearchRequestImpl( final int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // SearchRequest Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets a list of the attributes to be returned from each entry which
     * matches the search filter. There are two special values which may be
     * used: an empty list with no attributes, and the attribute description
     * string "*".  Both of these signify that all user attributes are to be
     * returned.  (The "*" allows the client to request all user attributes in
     * addition to specific operational attributes).
     *
     * Attributes MUST be named at most once in the list, and are returned at
     * most once in an entry.   If there are attribute descriptions in the list
     * which are not recognized, they are ignored by the server.
     *
     * If the client does not want any attributes returned, it can specify a
     * list containing only the attribute with OID "1.1".  This OID was chosen
     * arbitrarily and does not correspond to any attribute in use.
     *
     * Client implementors should note that even if all user attributes are
     * requested, some attributes of the entry may not be included in search
     * results due to access control or other restrictions.  Furthermore,
     * servers will not return operational attributes, such as objectClasses or
     * attributeTypes, unless they are listed by name, since there may be
     * extremely large number of values for certain operational attributes.
     * 
     * @return the collection of attributes to return for each entry
     */
    public Collection getAttributes()
    {
        return Collections.unmodifiableCollection( attributes );
    }


    /**
     * Gets the search base as a distinguished name.
     *
     * @return the search base
     */
    public String getBase()
    {
        return baseDn;
    }


    /**
     * Sets the search base as a distinguished name.
     *
     * @param base the search base
     */
    public void setBase( String base )
    {
        lockCheck( "Attempt to alter search base of locked SearchRequest!" );
        baseDn = base;
    }


    /**
     * Gets the alias handling parameter.
     *
     * @return the alias handling parameter enumeration.
     */
    public DerefAliasesEnum getDerefAliases()
    {
        return derefAliases;
    }


    /**
     * Sets the alias handling parameter.
     *
     * @param derefAliases the alias handling parameter enumeration.
     */
    public void setDerefAliases( DerefAliasesEnum derefAliases )
    {
        lockCheck(
        "Attempt to alter alias dereferencing mode of locked SearchRequest!" );
        this.derefAliases = derefAliases;
    }


    /**
     * Gets the search filter associated with this search request.
     *
     * @return the expression node for the root of the filter expression tree.
     */
    public ExprNode getFilter()
    {
        return filter;
    }


    /**
     * Sets the search filter associated with this search request.
     *
     * @param filter the expression node for the root of the filter expression
     * tree.
     */
    public void setFilter( ExprNode filter )
    {
        lockCheck( "Attempt to alter search filter of locked SearchRequest!" );
        this.filter = filter;
    }


    /**
     * Gets the different response types generated by a search request.
     *
     * @return the RESPONSE_TYPES array
     * @see RESPONSE_TYPES
     */
    public MessageTypeEnum [] getResponseTypes()
    {
        return ( MessageTypeEnum [] ) RESPONSE_TYPES.clone();
    }


    /**
     * Gets the search scope parameter enumeration.
     *
     * @return the scope enumeration parameter.
     */
    public ScopeEnum getScope()
    {
        return scope;
    }


    /**
     * Sets the search scope parameter enumeration.
     *
     * @param scope the scope enumeration parameter.
     */
    public void setScope( ScopeEnum scope )
    {
        lockCheck( "Attempt to alter search scope of locked SearchReqest!" );
        this.scope = scope;
    }


    /**
     * A sizelimit that restricts the maximum number of entries to be returned
     * as a result of the search. A value of 0 in this field indicates that no
     * client-requested sizelimit restrictions are in effect for the search.
     * Servers may enforce a maximum number of entries to return.
     * 
     * @return search size limit.
     */
    public int getSizeLimit()
    {
        return sizeLimit;
    }


    /**
     * Sets sizelimit that restricts the maximum number of entries to be
     * returned as a result of the search. A value of 0 in this field indicates
     * that no client-requested sizelimit restrictions are in effect for the
     * search.  Servers may enforce a maximum number of entries to return.
     * 
     * @param entriesMax maximum search result entries to return.
     */
    public void setSizeLimit( int entriesMax )
    {
        lockCheck( "Attempt to alter size limit on locked SearchRequest!" );
        sizeLimit = entriesMax;
    }


    /**
     * Gets the timelimit that restricts the maximum time (in seconds) allowed
     * for a search. A value of 0 in this field indicates that no client-
     * requested timelimit restrictions are in effect for the search.
     *
     * @return the search time limit in seconds.
     */
    public int getTimeLimit()
    {
        return timeLimit;
    }


    /**
     * Sets the timelimit that restricts the maximum time (in seconds) allowed
     * for a search. A value of 0 in this field indicates that no client-
     * requested timelimit restrictions are in effect for the search.
     *
     * @param secondsMax the search time limit in seconds.
     */
    public void setTimeLimit( int secondsMax )
    {
        lockCheck( "Attempt to alter time limit on locked SearchRequest!" );
        timeLimit = secondsMax;
    }


    /**
     * An indicator as to whether search results will contain both attribute
     * types and values, or just attribute types.  Setting this field to TRUE
     * causes only attribute types (no values) to be returned.  Setting this
     * field to FALSE causes both attribute types and values to be returned.
     *
     * @return true for only types, false for types and values.
     */
    public boolean getTypesOnly()
    {
        return typesOnly;
    }


    /**
     * An indicator as to whether search results will contain both attribute
     * types and values, or just attribute types.  Setting this field to TRUE
     * causes only attribute types (no values) to be returned.  Setting this
     * field to FALSE causes both attribute types and values to be returned.
     *
     * @param typesOnly true for only types, false for types and values.
     */
    public void setTypesOnly( boolean typesOnly )
    {
        lockCheck(
            "Attempt to alter typesOnly flag of locked SearchRequest!" );
        this.typesOnly = typesOnly;
    }


    /**
     * Adds an attribute to the set of entry attributes to return.
     *
     * @param attribute the attribute description or identifier.
     */
    public void addAttribute( String attribute )
    {
        lockCheck(
            "Attempt to add return attribute to locked SearchRequest!" );
        attributes.add( attribute );
    }


    /**
     * Removes an attribute to the set of entry attributes to return.
     *
     * @param attribute the attribute description or identifier.
     */
    public void removeAttribute( String attribute )
    {
        lockCheck(
            "Attempt to remove return attribute from locked SearchRequest!" );
        attributes.remove( attribute );
    }
    

    /**
     * The result containing response for this request.
     * 
     * @return the result containing response for this request
     */
    public ResultResponse getResultResponse()
    {
        if ( response == null )
        {
            response = new SearchResponseDoneImpl( getMessageId() );
        }
        
        return response;
    }


    /**
     * Checks to see if two search requests are equal.  The Lockable properties
     * and the get/set context specific parameters are not consulted to
     * determine equality.  The filter expression tree comparison will
     * normalize the child order of filter branch nodes then generate a string
     * representation which is comparable.  For the time being this is a very
     * costly operation.
     *
     * @param obj the object to check for equality to this SearchRequest
     * @return true if the obj is a SearchRequest and equals this
     * SearchRequest, false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ! super.equals( obj ) )
        {
            return false;
        }

        SearchRequest req = ( SearchRequest ) obj;

        if ( ! req.getBase().equals( baseDn ) )
        {
            return false;
        }

        if ( req.getDerefAliases() != derefAliases )
        {
             return false;
        }

        if ( req.getScope() != scope )
        {
            return false;
        }

        if ( req.getSizeLimit() != sizeLimit )
        {
            return false;
        }

        if ( req.getTimeLimit() != timeLimit )
        {
            return false;
        }

        if ( req.getTypesOnly() != typesOnly )
        {
            return false;
        }

        if ( req.getAttributes() == null && attributes != null )
        {
            if ( attributes.size() > 0 )
            {
                return false;
            }
        }

        if ( req.getAttributes() != null && attributes == null )
        {
            if ( req.getAttributes().size() > 0 )
            {
                return false;
            }
        }

        if ( req.getAttributes() != null && attributes != null )
        {
            if ( req.getAttributes().size() != attributes.size() )
            {
                return false;
            }

            Iterator list = attributes.iterator();
            while ( list.hasNext() )
            {
                if ( ! req.getAttributes().contains( list.next() ) )
                {
                    return false;
                }
            }
        }

        BranchNormalizedVisitor visitor = new BranchNormalizedVisitor();
        req.getFilter().accept( visitor );
        filter.accept( visitor );

        StringBuffer buf = new StringBuffer();
        filter.printToBuffer( buf );
        String myFilterString = buf.toString();

        buf.setLength( 0 );
        req.getFilter().printToBuffer( buf );
        String reqFilterString = buf.toString();

        return myFilterString.equals( reqFilterString );
    }
}
