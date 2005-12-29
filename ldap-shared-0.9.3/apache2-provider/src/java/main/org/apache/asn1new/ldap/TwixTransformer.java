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
package org.apache.asn1new.ldap;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.Asn1Object;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.primitives.LdapDN;
import org.apache.asn1new.ldap.codec.primitives.LdapString;
import org.apache.asn1new.ldap.codec.primitives.LdapStringEncodingException;
import org.apache.asn1new.ldap.codec.primitives.LdapURL;
import org.apache.asn1new.ldap.codec.primitives.LdapURLEncodingException;
import org.apache.asn1new.ldap.pojo.AbandonRequest;
import org.apache.asn1new.ldap.pojo.AddRequest;
import org.apache.asn1new.ldap.pojo.AddResponse;
import org.apache.asn1new.ldap.pojo.AttributeValueAssertion;
import org.apache.asn1new.ldap.pojo.BindRequest;
import org.apache.asn1new.ldap.pojo.BindResponse;
import org.apache.asn1new.ldap.pojo.CompareRequest;
import org.apache.asn1new.ldap.pojo.CompareResponse;
import org.apache.asn1new.ldap.pojo.Control;
import org.apache.asn1new.ldap.pojo.DelRequest;
import org.apache.asn1new.ldap.pojo.DelResponse;
import org.apache.asn1new.ldap.pojo.ExtendedRequest;
import org.apache.asn1new.ldap.pojo.ExtendedResponse;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.ldap.pojo.LdapResult;
import org.apache.asn1new.ldap.pojo.ModifyDNRequest;
import org.apache.asn1new.ldap.pojo.ModifyDNResponse;
import org.apache.asn1new.ldap.pojo.ModifyRequest;
import org.apache.asn1new.ldap.pojo.ModifyResponse;
import org.apache.asn1new.ldap.pojo.SaslCredentials;
import org.apache.asn1new.ldap.pojo.SearchRequest;
import org.apache.asn1new.ldap.pojo.SearchResultDone;
import org.apache.asn1new.ldap.pojo.SearchResultEntry;
import org.apache.asn1new.ldap.pojo.SearchResultReference;
import org.apache.asn1new.ldap.pojo.SimpleAuthentication;
import org.apache.asn1new.ldap.pojo.filters.AndFilter;
import org.apache.asn1new.ldap.pojo.filters.AttributeValueAssertionFilter;
import org.apache.asn1new.ldap.pojo.filters.ConnectorFilter;
import org.apache.asn1new.ldap.pojo.filters.ExtensibleMatchFilter;
import org.apache.asn1new.ldap.pojo.filters.Filter;
import org.apache.asn1new.ldap.pojo.filters.NotFilter;
import org.apache.asn1new.ldap.pojo.filters.OrFilter;
import org.apache.asn1new.ldap.pojo.filters.PresentFilter;
import org.apache.asn1new.ldap.pojo.filters.SubstringFilter;
import org.apache.asn1new.primitives.OID;
import org.apache.asn1new.primitives.OctetString;
import org.apache.ldap.common.filter.AbstractExprNode;
import org.apache.ldap.common.filter.BranchNode;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.ExtensibleNode;
import org.apache.ldap.common.filter.LeafNode;
import org.apache.ldap.common.filter.PresenceNode;
import org.apache.ldap.common.filter.SimpleNode;
import org.apache.ldap.common.filter.SubstringNode;
import org.apache.ldap.common.message.AbandonRequestImpl;
import org.apache.ldap.common.message.AddRequestImpl;
import org.apache.ldap.common.message.AddResponseImpl;
import org.apache.ldap.common.message.BindRequestImpl;
import org.apache.ldap.common.message.BindResponseImpl;
import org.apache.ldap.common.message.CompareRequestImpl;
import org.apache.ldap.common.message.CompareResponseImpl;
import org.apache.ldap.common.message.ControlImpl;
import org.apache.ldap.common.message.DeleteRequestImpl;
import org.apache.ldap.common.message.DeleteResponseImpl;
import org.apache.ldap.common.message.DerefAliasesEnum;
import org.apache.ldap.common.message.ExtendedRequestImpl;
import org.apache.ldap.common.message.ExtendedResponseImpl;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.MessageTypeEnum;
import org.apache.ldap.common.message.ModifyDnRequestImpl;
import org.apache.ldap.common.message.ModifyDnResponseImpl;
import org.apache.ldap.common.message.ModifyRequestImpl;
import org.apache.ldap.common.message.ModifyResponseImpl;
import org.apache.ldap.common.message.Referral;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.ScopeEnum;
import org.apache.ldap.common.message.SearchRequestImpl;
import org.apache.ldap.common.message.SearchResponseDoneImpl;
import org.apache.ldap.common.message.SearchResponseEntryImpl;
import org.apache.ldap.common.message.SearchResponseReferenceImpl;
import org.apache.ldap.common.message.UnbindRequestImpl;
import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.TransformerSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Twix to Snickers Message transformer. 
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev: 157671 $
 */
public class TwixTransformer implements TransformerSpi
{
	/** The logger */
    private static Logger log = LoggerFactory.getLogger( TwixTransformer.class );
    
    /** the provider this transformer is part of */
    private final Provider provider;


    /**
     * Creates a passthrough transformer that really does nothing at all.
     *
     * @param provider the povider for this transformer
     */
    public TwixTransformer( Provider provider )
    {
        this.provider = provider;
    }


    /**
     * Gets the Provider associated with this SPI implementation object.
     *
     * @return Provider.
     */
    public Provider getProvider()
    {
        return provider;
    }

    /**
     * Transform an AbandonRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers AbandonRequestImpl
     */
    private Message transformAbandonRequest(LdapMessage twixMessage, int messageId)
    {
    	AbandonRequestImpl snickersMessage = new AbandonRequestImpl( messageId ) ;
    	AbandonRequest abandonRequest = twixMessage.getAbandonRequest();

    	// Twix : int abandonnedMessageId -> Snickers : int abandonId
    	snickersMessage.setAbandoned( abandonRequest.getAbandonedMessageId() );
    	
    	return snickersMessage;
    }
    
    /**
     * Transform an AddRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers AddRequestImpl
     */
    private Message transformAddRequest(LdapMessage twixMessage, int messageId)
    {
    	AddRequestImpl snickersMessage = new AddRequestImpl( messageId ) ;
    	AddRequest addRequest = twixMessage.getAddRequest();

    	// Twix : LdapDN entry -> Snickers : String name
    	snickersMessage.setName( addRequest.getEntry() );
    	
    	// Twix : Attributes attributes -> Snickers : Attributes entry
    	snickersMessage.setEntry( addRequest.getAttributes() );
    	
    	return snickersMessage;
    }
    
    /**
     * Transform a BindRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers BindRequestImpl
     */
    private Message transformBindRequest(LdapMessage twixMessage, int messageId)
    {
    	BindRequestImpl snickersMessage = new BindRequestImpl( messageId ) ;
    	BindRequest bindRequest = twixMessage.getBindRequest();
    	
    	// Twix : int version -> Snickers : boolean isVersion3
    	snickersMessage.setVersion3( bindRequest.isLdapV3() );
    	
    	// Twix : LdapDN name -> Snickers : String name
    	snickersMessage.setName( bindRequest.getName() );
    	
    	// Twix : Asn1Object authentication instanceOf SimpleAuthentication -> Snickers : boolean isSimple
    	// Twix : SimpleAuthentication OctetString simple -> Snickers : byte [] credentials
    	Asn1Object authentication = bindRequest.getAuthentication();
    	
    	if ( authentication instanceof SimpleAuthentication)
    	{
    		snickersMessage.setSimple( true );
    		snickersMessage.setCredentials( ( (SimpleAuthentication)authentication).getSimple().getValue() );
    	}
    	else
    	{
    		snickersMessage.setSimple( false );
    		snickersMessage.setCredentials( ( (SaslCredentials)authentication).getCredentials().getValue() );
    		snickersMessage.setSaslMechanism( ( (SaslCredentials)authentication).getMechanism() );
    	}

    	return snickersMessage;
    }
    
    /**
     * Transform a CompareRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers CompareRequestImpl
     */
    private Message transformCompareRequest(LdapMessage twixMessage, int messageId)
    {
    	CompareRequestImpl snickersMessage = new CompareRequestImpl( messageId ) ;
    	CompareRequest compareRequest = twixMessage.getCompareRequest();

    	// Twix : LdapDN entry -> Snickers : private String name
    	snickersMessage.setName( compareRequest.getEntry() );
    	
    	// Twix : LdapString attributeDesc -> Snickers : String attrId
    	snickersMessage.setAttributeId( compareRequest.getAttributeDesc() );
    	
    	// Twix : OctetString assertionValue -> Snickers : byte[] attrVal
    	snickersMessage.setAssertionValue( compareRequest.getAssertionValue().getValue() );
    	
    	return snickersMessage;
    }
    
    /**
     * Transform a DelRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers DeleteRequestImpl
     */
    private Message transformDelRequest(LdapMessage twixMessage, int messageId)
    {
    	DeleteRequestImpl snickersMessage = new DeleteRequestImpl( messageId ) ;
    	DelRequest delRequest = twixMessage.getDelRequest();

    	// Twix : LdapDN entry -> Snickers : String name
    	snickersMessage.setName( delRequest.getEntry() );
    	
    	return snickersMessage;
    }
    
    /**
     * Transform an ExtendedRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers ExtendedRequestImpl
     */
    private Message transformExtendedRequest(LdapMessage twixMessage, int messageId)
    {
    	ExtendedRequestImpl snickersMessage = new ExtendedRequestImpl( messageId ) ;
    	ExtendedRequest extendedRequest = twixMessage.getExtendedRequest();

    	// Twix : OID requestName -> Snickers : String oid
    	snickersMessage.setOid( extendedRequest.getRequestName() );
    	
    	// Twix : OctetString requestValue -> Snickers : byte [] payload
    	snickersMessage.setPayload( extendedRequest.getRequestValue().getValue() );

    	return snickersMessage;
    }
    
    /**
     * Transform a ModifyDNRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers ModifyDNRequestImpl
     */
    private Message transformModifyDNRequest(LdapMessage twixMessage, int messageId)
    {
    	ModifyDnRequestImpl snickersMessage = new ModifyDnRequestImpl( messageId ) ;
    	ModifyDNRequest modifyDNRequest = twixMessage.getModifyDNRequest();

    	// Twix : LdapDN entry -> Snickers : String m_name
    	snickersMessage.setName( modifyDNRequest.getEntry() );
    	
    	// Twix : RelativeLdapDN newRDN -> Snickers : String m_newRdn
    	snickersMessage.setNewRdn( modifyDNRequest.getNewRDN() );

    	// Twix : boolean deleteOldRDN -> Snickers : boolean m_deleteOldRdn
    	snickersMessage.setDeleteOldRdn( modifyDNRequest.isDeleteOldRDN() );
    	
    	// Twix : LdapDN newSuperior -> Snickers : String m_newSuperior
    	snickersMessage.setNewSuperior( modifyDNRequest.getNewSuperior() );

    	return snickersMessage;
    }
    
    /**
     * Transform a ModifyRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers ModifyRequestImpl
     */
    private Message transformModifyRequest(LdapMessage twixMessage, int messageId)
    {
    	ModifyRequestImpl snickersMessage = new ModifyRequestImpl( messageId ) ;
    	ModifyRequest modifyRequest = twixMessage.getModifyRequest();

    	// Twix : LdapDN object -> Snickers : String name
    	snickersMessage.setName( modifyRequest.getObject() );
    	
    	// Twix : ArrayList modifications -> Snickers : ArrayList mods
    	if ( modifyRequest.getModifications() != null )
    	{
	    	Iterator modifications = modifyRequest.getModifications().iterator();
	   
	    	// Loop through the modifications
	    	while (modifications.hasNext())
	    	{
	    		snickersMessage.addModification( (ModificationItem)modifications.next() );
	    	}
    	}

    	return snickersMessage;
    }
    
    /**
     * Transform the Filter part of a SearchRequest to en ExprNode
     * @param twixFilter The filter to be transformed
     * @return An ExprNode
     */
    private ExprNode transformFilter(Filter twixFilter)
    {
    	if ( twixFilter != null )
    	{
    		// Transform OR, AND or NOT leaves
    		if ( twixFilter instanceof ConnectorFilter )
    		{
    			BranchNode branch = null;
    			
    			if ( twixFilter instanceof AndFilter )
    			{
    				branch = new BranchNode( BranchNode.AND );
    			}
    			else if ( twixFilter instanceof OrFilter )
    			{
    				branch = new BranchNode( BranchNode.OR ); 
    			}
    			else if ( twixFilter instanceof NotFilter )
    			{
    				branch = new BranchNode( BranchNode.NOT ); 
    			}
    			
				ArrayList filtersSet = ((ConnectorFilter)twixFilter).getFilterSet();
				
				// Loop on all AND/OR children
				if ( filtersSet != null )
				{
					Iterator filters = filtersSet.iterator();
					
					while ( filters.hasNext() )
					{
						branch.addNode( transformFilter( (Filter)filters.next() ) );
					}
				}

				return branch;
    		}
    		else
    		{
    			// Transform PRESENT or ATTRIBUTE_VALUE_ASSERTION
    			LeafNode branch = null;
    			
    			if ( twixFilter instanceof PresentFilter )
    			{
    				branch = new PresenceNode( ( (PresentFilter) twixFilter ).getAttributeDescription().toString() );
    			}
    			else if ( twixFilter instanceof AttributeValueAssertionFilter )
    			{
    				AttributeValueAssertion ava = ( (AttributeValueAssertionFilter) twixFilter ).getAssertion();
    				
    				// Transform =, >=, <=, ~= filters
    				switch ( ( (AttributeValueAssertionFilter) twixFilter ).getFilterType() )
    				{
    					case LdapConstants.EQUALITY_MATCH_FILTER : 
    	    				branch = new SimpleNode( ava.getAttributeDesc().toString(), 
    	    										 ava.getAssertionValue().toString(), 
    	    										 AbstractExprNode.EQUALITY );
    	    				break;
    						
    					case LdapConstants.GREATER_OR_EQUAL_FILTER : 
    	    				branch = new SimpleNode( ava.getAttributeDesc().toString(), 
    	    										 ava.getAssertionValue().toString(), 
    	    										 AbstractExprNode.GREATEREQ );
    	    				break;

    					case LdapConstants.LESS_OR_EQUAL_FILTER : 
    	    				branch = new SimpleNode( ava.getAttributeDesc().toString(), 
    	    										 ava.getAssertionValue().toString(), 
    	    										 AbstractExprNode.LESSEQ );
    	    				break;

    					case LdapConstants.APPROX_MATCH_FILTER : 
    	    				branch = new SimpleNode( ava.getAttributeDesc().toString(), 
    	    										 ava.getAssertionValue().toString(), 
    	    										 AbstractExprNode.APPROXIMATE );
    	    				break;
    				}
    				
    			}
    			else if ( twixFilter instanceof SubstringFilter )
    			{
    				// Transform Substring filters
    				SubstringFilter filter = (SubstringFilter)twixFilter;
    				String initialString = null;
    				String finalString = null;
    				ArrayList anyString = null;
    				
    				
    				if ( filter.getInitialSubstrings() != null )
    				{
    					initialString = filter.getInitialSubstrings().toString();
    				}
    				
    				if ( filter.getFinalSubstrings() != null )
    				{
    					finalString = filter.getFinalSubstrings().toString();
    				}
    				
    				if ( filter.getAnySubstrings() != null )
    				{
    					Iterator iter = filter.getAnySubstrings().iterator();
    					anyString = new ArrayList();
    					
    					while ( iter.hasNext() )
    					{
    						anyString.add( iter.next() );
    					}
    				}
    				
    				branch = new SubstringNode(anyString, filter.getType().toString(), initialString, finalString );
    			}
    			else if ( twixFilter instanceof ExtensibleMatchFilter )
    			{
    				// Transform Extensible Match Filter
    				ExtensibleMatchFilter filter = (ExtensibleMatchFilter)twixFilter;
    				String attribute = null;
    				String value = null;
    				String matchingRule = null;
    				
    				if ( filter.getType() != null )
    				{
    					attribute = filter.getType().toString(); 
    				}

    				if ( filter.getMatchValue() != null )
    				{
    					value = filter.getMatchValue().toString(); 
    				}

    				if ( filter.getMatchingRule() != null )
    				{
    					matchingRule = filter.getMatchingRule().toString(); 
    				}

    				branch = new ExtensibleNode( attribute, value, matchingRule, filter.isDnAttributes() );
    			}
    			
    			return branch;
    		}
    	}
    	else
    	{
    		// We have found nothing to transform. Return null then.
    		return null;
    	}
    }

    /**
     * Transform a SearchRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers SearchRequestImpl
     */
    private Message transformSearchRequest(LdapMessage twixMessage, int messageId)
    {
    	SearchRequestImpl snickersMessage = new SearchRequestImpl( messageId ) ;
    	SearchRequest searchRequest = twixMessage.getSearchRequest();
    	
    	// Twix : LdapDN baseObject -> Snickers : String baseDn
    	snickersMessage.setBase( searchRequest.getBaseObject() );
    	
    	// Twix : int scope -> Snickers : ScopeEnum scope
    	switch ( searchRequest.getScope() )
    	{
    		case LdapConstants.SCOPE_BASE_OBJECT :
    			snickersMessage.setScope( ScopeEnum.BASEOBJECT );
    			break;
    			
    		case LdapConstants.SCOPE_SINGLE_LEVEL :
    			snickersMessage.setScope( ScopeEnum.SINGLELEVEL );
    			break;
    			
    		case LdapConstants.SCOPE_WHOLE_SUBTREE :
    			snickersMessage.setScope( ScopeEnum.WHOLESUBTREE );
    			break;
    	}
    	
    	// Twix : int derefAliases -> Snickers : DerefAliasesEnum derefAliases
    	switch ( searchRequest.getDerefAliases() )
    	{
    		case LdapConstants.DEREF_ALWAYS :
    			snickersMessage.setDerefAliases( DerefAliasesEnum.DEREFALWAYS );
    			break;
    			
    		case LdapConstants.DEREF_FINDING_BASE_OBJ :
    			snickersMessage.setDerefAliases( DerefAliasesEnum.DEREFFINDINGBASEOBJ );
    			break;
    			
    		case LdapConstants.DEREF_IN_SEARCHING :
    			snickersMessage.setDerefAliases( DerefAliasesEnum.DEREFINSEARCHING );
    			break;

    		case LdapConstants.NEVER_DEREF_ALIASES :
    			snickersMessage.setDerefAliases( DerefAliasesEnum.NEVERDEREFALIASES );
    			break;
    	}
    	
    	// Twix : int sizeLimit -> Snickers : int sizeLimit
    	snickersMessage.setSizeLimit( searchRequest.getSizeLimit() );

    	// Twix : int timeLimit -> Snickers : int timeLimit
    	snickersMessage.setTimeLimit( searchRequest.getTimeLimit() );

    	// Twix : boolean typesOnly -> Snickers : boolean typesOnly
    	snickersMessage.setTypesOnly( searchRequest.isTypesOnly() );

    	// Twix : Filter filter -> Snickers : ExprNode filter
    	Filter twixFilter = searchRequest.getFilter();
    	
    	snickersMessage.setFilter( transformFilter( twixFilter ) );
    	
    	// Twix : ArrayList attributes -> Snickers : ArrayList attributes
    	if ( searchRequest.getAttributes() != null )
    	{
	    	NamingEnumeration attributes = searchRequest.getAttributes().getAll();
	    	
	    	if ( attributes != null )
	    	{
	    		while ( attributes.hasMoreElements() )
	    		{
	    			Attribute attribute = (BasicAttribute)attributes.nextElement();
	    			
	    			if ( attribute != null )
	    			{
		    	    	snickersMessage.addAttribute( attribute.getID() );
	    			}
	    		}
	    	}
    	}
    	
    	return snickersMessage;
    }

    /**
     * Transform an UnBindRequest message from a TwixMessage to a SnickersMessage
     * @param twixMessage The message to transform
     * @param messageId The message Id
     * @return A Snickers UnBindRequestImpl
     */
    private Message transformUnBindRequest(LdapMessage twixMessage, int messageId)
    {
    	return new UnbindRequestImpl( messageId ) ;
    }
    
    /**
     * Transform the Twix message to a Snickers message.
     *
     * @param obj the object to transform
     * @return the object transformed
     */
    public Message transform( Object obj )
    {
    	LdapMessage twixMessage = (LdapMessage) obj;
    	int messageId = twixMessage.getMessageId();
    	
    	if ( log.isDebugEnabled() )
    	{
    		log.debug( "Transforming LdapMessage <" + messageId + ", " + twixMessage.getMessageTypeName() + "> from Twix to Snickers.");
    	}
    	
        Message snickersMessage = null ;
        
        int messageType = twixMessage.getMessageType();

        switch ( messageType )
        {
            case( LdapConstants.BIND_REQUEST ):
            	snickersMessage = transformBindRequest( twixMessage, messageId );
                break ;
                
            case( LdapConstants.UNBIND_REQUEST ):
            	snickersMessage = transformUnBindRequest( twixMessage, messageId );
                break ;
                
            case( LdapConstants.SEARCH_REQUEST ):
            	snickersMessage = transformSearchRequest( twixMessage, messageId );
                break ;
                
            case( LdapConstants.MODIFY_REQUEST ):
            	snickersMessage = transformModifyRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.ADD_REQUEST ):
            	snickersMessage = transformAddRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.DEL_REQUEST ):
            	snickersMessage = transformDelRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.MODIFYDN_REQUEST ):
            	snickersMessage = transformModifyDNRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.COMPARE_REQUEST ):
            	snickersMessage = transformCompareRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.ABANDON_REQUEST ):
            	snickersMessage = transformAbandonRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.EXTENDED_REQUEST ):
            	snickersMessage = transformExtendedRequest( twixMessage, messageId ) ;
                break ;
                
            case( LdapConstants.BIND_RESPONSE ):
            case( LdapConstants.SEARCH_RESULT_ENTRY ):
            case( LdapConstants.SEARCH_RESULT_DONE ):
            case( LdapConstants.SEARCH_RESULT_REFERENCE ):
            case( LdapConstants.MODIFY_RESPONSE ):
            case( LdapConstants.ADD_RESPONSE ):
            case( LdapConstants.DEL_RESPONSE ):
            case( LdapConstants.MODIFYDN_RESPONSE ):
            case( LdapConstants.COMPARE_RESPONSE ):
            case( LdapConstants.EXTENDED_RESPONSE ):
            	// Nothing to do !
                break ;
                
            default:
                throw new IllegalStateException(
                        "shouldn't happen - if it does then we have issues" ) ;
        }
        
        // Transform the controls, too
        ArrayList twixControls = twixMessage.getControls();
        
        if ( twixControls != null )
        {
        	Iterator controls = twixControls.iterator();
        	
        	while ( controls.hasNext() )
        	{
        		Control twixControl = (Control)controls.next();
        		
                ControlImpl snickersControl = new ControlImpl( snickersMessage )
                {
                	// Just to avoid a compilation warning !!!
                	public static final long serialVersionUID = 1L;
                	
                	
                    public byte[] getEncodedValue()
                    {
                        return null;
                    }        	
                };
                
            	// Twix : boolean criticality -> Snickers : boolean m_isCritical
                snickersControl.setCritical( twixControl.getCriticality() );

            	// Twix : OID controlType -> Snickers : String m_oid
                snickersControl.setType( twixControl.getControlType() );
                
            	// Twix : OctetString controlValue -> Snickers : byte [] m_value
                snickersControl.setValue( twixControl.getControlValue() );
                
                snickersMessage.add( snickersControl );
        	}
        }

        return  snickersMessage;
    }

    /**
     * Transform a Ldapresult part of a Snickers Response to a Twix LdapResult
     * @param snickersLdapResult The Snickers LdapResult to transform
     * @return A Twix LdapResult
     */
    private LdapResult transformLdapResult(LdapResultImpl snickersLdapResult)
    {
		LdapResult twixLdapResult = new LdapResult();
		
	    // Snickers : ResultCodeEnum resultCode -> Twix : int resultCode
		twixLdapResult.setResultCode( snickersLdapResult.getResultCode().getValue() );
		
	    // Snickers : String errorMessage -> Twix : LdapString errorMessage
		try 
		{
			String errorMessage = snickersLdapResult.getErrorMessage();
			
			if ( ( errorMessage == null ) || ( errorMessage.length() == 0 ) )
			{
				twixLdapResult.setErrorMessage( LdapString.EMPTY_STRING );
			}
			else
			{
				twixLdapResult.setErrorMessage( new LdapString( snickersLdapResult.getErrorMessage().getBytes() ) );
			}
		}
		catch (LdapStringEncodingException lsee ) 
		{
			log.warn( "The error message " + snickersLdapResult.getErrorMessage() + " is invalid : " + lsee.getMessage() );
			twixLdapResult.setErrorMessage( LdapString.EMPTY_STRING );
		}

	    // Snickers : String matchedDn -> Twix : LdapDN matchedDN
		try 
		{
			String matchedDn = snickersLdapResult.getMatchedDn();

			if ( ( matchedDn == null ) || ( matchedDn.length() == 0 ) )
			{
				twixLdapResult.setMatchedDN( LdapDN.EMPTY_LDAPDN );
			}
			else
			{
				twixLdapResult.setMatchedDN( new LdapDN( snickersLdapResult.getMatchedDn() ) );
			}
		}
		catch ( InvalidNameException ine ) 
		{
			log.warn( "The DN  " + snickersLdapResult.getMatchedDn() + " is invalid : " + ine.getMessage() );
			twixLdapResult.setMatchedDN( LdapDN.EMPTY_LDAPDN );
		}

	    // Snickers : Referral referral -> Twix : ArrayList referrals
		ReferralImpl snisckersReferrals = (ReferralImpl)snickersLdapResult.getReferral();
		
		if ( snisckersReferrals != null )
		{
			Iterator referrals = snisckersReferrals.getLdapUrls().iterator();
			
			while ( referrals.hasNext() )
			{
				String referral = (String)referrals.next();
				
	    		try 
	    		{
	    			LdapURL ldapUrl = new LdapURL( referral.getBytes() );
	    			twixLdapResult.addReferral( ldapUrl );
	    		}
				catch ( LdapURLEncodingException lude )
				{
					log.warn( "The referral " + referral + " is invalid : " + lude.getMessage() );
					twixLdapResult.addReferral( LdapURL.EMPTY_URL );
				}
			}
		}
		
		return twixLdapResult;
    }

    /**
     * Transform a Snickers AddResponse to a Twix AddResponse
     * @param twixMessage The Twix AddResponse to produce 
     * @param snickersMessage The incoming Snickers AddResponse
     */
    private void transformAddResponse( LdapMessage twixMessage, Message snickersMessage )
    {
		AddResponseImpl snickersAddResponse = (AddResponseImpl)snickersMessage;
		
		AddResponse addResponse = new AddResponse();
		
		// Transform the ldapResult
		addResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersAddResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( addResponse );
    }
    
    /**
     * Transform a Snickers BindResponse to a Twix BindResponse
     * @param twixMessage The Twix BindResponse to produce 
     * @param snickersMessage The incoming Snickers BindResponse
     */
    private void transformBindResponse( LdapMessage twixMessage, Message snickersMessage )
    {
		BindResponseImpl snickersBindResponse = (BindResponseImpl)snickersMessage;
		
		BindResponse bindResponse = new BindResponse();
		
	    // Snickers : byte [] serverSaslCreds -> Twix : OctetString serverSaslCreds
		byte[] serverSaslCreds = snickersBindResponse.getServerSaslCreds();
		
		if ( serverSaslCreds != null )
		{
    		bindResponse.setServerSaslCreds( new OctetString( serverSaslCreds ) );
		}
		
		// Transform the ldapResult
		bindResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersBindResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( bindResponse );
    }
    
    /**
     * Transform a Snickers CompareResponse to a Twix CompareResponse
     * @param twixMessage The Twix CompareResponse to produce 
     * @param snickersMessage The incoming Snickers CompareResponse
     */
    private void transformCompareResponse( LdapMessage twixMessage, Message snickersMessage )
    {
    	CompareResponseImpl snickersCompareResponse = (CompareResponseImpl)snickersMessage;
		
    	CompareResponse compareResponse = new CompareResponse();
		
		// Transform the ldapResult
    	compareResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersCompareResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( compareResponse );
    }
    
    /**
     * Transform a Snickers DelResponse to a Twix DelResponse
     * @param twixMessage The Twix DelResponse to produce 
     * @param snickersMessage The incoming Snickers DelResponse
     */
    private void transformDelResponse( LdapMessage twixMessage, Message snickersMessage )
    {
    	DeleteResponseImpl snickersDelResponse = (DeleteResponseImpl)snickersMessage;
		
    	DelResponse delResponse = new DelResponse();
		
		// Transform the ldapResult
    	delResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersDelResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( delResponse );
    }
    
    /**
     * Transform a Snickers ExtendedResponse to a Twix ExtendedResponse
     * @param twixMessage The Twix ExtendedResponse to produce 
     * @param snickersMessage The incoming Snickers ExtendedResponse
     */
    private void transformExtendedResponse( LdapMessage twixMessage, Message snickersMessage )
    {
    	ExtendedResponseImpl snickersExtendedResponse = (ExtendedResponseImpl)snickersMessage;
		
    	ExtendedResponse extendedResponse = new ExtendedResponse();
		
	    // Snickers : String oid -> Twix : OID responseName
    	try 
    	{
    		extendedResponse.setResponseName( new OID( snickersExtendedResponse.getResponseName() ) );
    	}
    	catch ( DecoderException de )
    	{
    		log.warn( "The OID " + snickersExtendedResponse.getResponseName() + " is invalid : " + de.getMessage() );
    		extendedResponse.setResponseName( null );
    	}

	    // Snickers : byte [] value -> Twix : OctetString response
    	extendedResponse.setResponse( new OctetString( snickersExtendedResponse.getResponse() ) );

    	// Transform the ldapResult
    	extendedResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersExtendedResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( extendedResponse );
    }
    
    /**
     * Transform a Snickers ModifyResponse to a Twix ModifyResponse
     * @param twixMessage The Twix ModifyResponse to produce 
     * @param snickersMessage The incoming Snickers ModifyResponse
     */
    private void transformModifyResponse( LdapMessage twixMessage, Message snickersMessage )
    {
    	ModifyResponseImpl snickersModifyResponse = (ModifyResponseImpl)snickersMessage;
		
    	ModifyResponse modifyResponse = new ModifyResponse();
		
    	// Transform the ldapResult
    	modifyResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersModifyResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( modifyResponse );
    }
    
    /**
     * Transform a Snickers ModifyDNResponse to a Twix ModifyDNResponse
     * @param twixMessage The Twix ModifyDNResponse to produce 
     * @param snickersMessage The incoming Snickers ModifyDNResponse
     */
    private void transformModifyDNResponse( LdapMessage twixMessage, Message snickersMessage )
    {
    	ModifyDnResponseImpl snickersModifyDNResponse = (ModifyDnResponseImpl)snickersMessage;
		
    	ModifyDNResponse modifyDNResponse = new ModifyDNResponse();
		
    	// Transform the ldapResult
    	modifyDNResponse.setLdapResult( transformLdapResult( (LdapResultImpl)snickersModifyDNResponse.getLdapResult() ) );
		
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( modifyDNResponse );
    }
    
    /**
     * Transform a Snickers SearchResponseDone to a Twix SearchResultDone
     * @param twixMessage The Twix SearchResultDone to produce 
     * @param snickersMessage The incoming Snickers SearchResponseDone
     */
    private void transformSearchResultDone( LdapMessage twixMessage, Message snickersMessage )
    {
    	SearchResponseDoneImpl snickersSearchResponseDone = (SearchResponseDoneImpl)snickersMessage;
    	SearchResultDone searchResultDone = new SearchResultDone(); 
    	
		// Transform the ldapResult
    	searchResultDone.setLdapResult( transformLdapResult( (LdapResultImpl)snickersSearchResponseDone.getLdapResult() ) );

		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( searchResultDone );
    }
    
    /**
     * Transform a Snickers SearchResponseEntry to a Twix SearchResultEntry
     * @param twixMessage The Twix SearchResultEntry to produce 
     * @param snickersMessage The incoming Snickers SearchResponseEntry
     */
    private void transformSearchResultEntry( LdapMessage twixMessage, Message snickersMessage )
    {
    	SearchResponseEntryImpl snickersSearchResultResponse = (SearchResponseEntryImpl)snickersMessage;
    	SearchResultEntry searchResultEntry = new SearchResultEntry(); 
    	
	    // Snickers : String dn -> Twix : LdapDN objectName
    	try 
    	{
    		searchResultEntry.setObjectName( new LdapDN( snickersSearchResultResponse.getObjectName().getBytes() ) );
    	}
    	catch (InvalidNameException ine)
    	{
			log.warn( "The DN " + snickersSearchResultResponse.getObjectName() + " is invalid : " + ine.getMessage() );
    		searchResultEntry.setObjectName( LdapDN.EMPTY_LDAPDN );
    	}
    	
    	// Snickers : Attributes attributes -> Twix : ArrayList partialAttributeList
    	searchResultEntry.setPartialAttributeList( snickersSearchResultResponse.getAttributes() );
    	
		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( searchResultEntry );
    }
    
    /**
     * Transform a Snickers SearchResponseReference to a Twix SearchResultReference
     * @param twixMessage The Twix SearchResultReference to produce 
     * @param snickersMessage The incoming Snickers SearchResponseReference
     */
    private void transformSearchResultReference( LdapMessage twixMessage, Message snickersMessage )
    {
    	SearchResponseReferenceImpl snickersSearchResponseReference = (SearchResponseReferenceImpl)snickersMessage;
    	SearchResultReference searchResultReference = new SearchResultReference(); 
    	
	    // Snickers : Referral m_referral -> Twix: ArrayList searchResultReferences
    	Referral referrals = snickersSearchResponseReference.getReferral();
    	
    	// Loop on all referals
    	if ( referrals != null )
    	{
    		Collection urls = referrals.getLdapUrls();
    		
    		if ( urls != null )
    		{
    			Iterator url = urls.iterator();
    			
    			while ( url.hasNext() )
    			{
					String urlValue = (String)url.next();

					try
    				{
    					searchResultReference.addSearchResultReference( new LdapURL( urlValue ) );
    				}
    				catch ( LdapURLEncodingException luee )
    				{
    					log.warn( "The LdapURL " + urlValue + " is incorrect : " + luee.getMessage() );
    				}
    			}
    		}
    	}

		// Set the operation into the LdapMessage
		twixMessage.setProtocolOP( searchResultReference );
    }
    
    /**
     * Transform the Snickers message to a Twix message.
     *
     * @param msg the message to transform
     * @return the msg transformed
     */
    public Object transform( Message msg )
    {
    	if (log.isDebugEnabled())
    	{
    		log.debug("Transforming message type " + msg.getType());
    	}
    	
    	LdapMessage twixMessage = new LdapMessage();
    	
    	twixMessage.setMessageId( msg.getMessageId() );
    	
    	if ( msg.getType() == MessageTypeEnum.SEARCHRESENTRY)
    	{
    		transformSearchResultEntry( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.SEARCHRESDONE)
    	{
    		transformSearchResultDone( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.SEARCHRESREF)
    	{
    		transformSearchResultReference( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.BINDRESPONSE)
    	{
    		transformBindResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.ADDRESPONSE)
    	{
    		transformAddResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.COMPARERESPONSE)
    	{
    		transformCompareResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.DELRESPONSE)
    	{
    		transformDelResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.MODIFYRESPONSE)
    	{
    		transformModifyResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.MODDNRESPONSE)
    	{
    		transformModifyDNResponse( twixMessage, msg );
    	}
    	else if ( msg.getType() == MessageTypeEnum.EXTENDEDRESP)
    	{
    		transformExtendedResponse( twixMessage, msg );
    	}
        
		// We also have to transform the controls...
		//transformControls( twixMessage, msg );
		
    	if (log.isDebugEnabled())
    	{
    		log.debug( "Transformed message : " + twixMessage );
    	}
    	
    	return twixMessage;
    }
}
