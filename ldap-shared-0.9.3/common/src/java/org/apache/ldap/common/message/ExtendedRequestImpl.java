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


import org.apache.ldap.common.util.ArrayUtils;


/**
 * Lockable ExtendedRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ExtendedRequestImpl
    extends AbstractRequest implements ExtendedRequest
{
    static final long serialVersionUID = 7916990159044177480L;
    /** Extended request's Object Identifier or <b>requestName</b> */
    private String oid;
    /** Extended request's payload or <b>requestValue</b> */
    private byte [] payload;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------


    /**
     * Creates a Lockable ExtendedRequest implementing object used to perform
     * extended protocol operation on the server.
     *
     * @param id the sequential message identifier
     */
    public ExtendedRequestImpl( final int id )
    {
        super( id, TYPE, true );
    }


    // -----------------------------------------------------------------------
    // ExtendedRequest Interface Method Implementations
    // -----------------------------------------------------------------------
    

    /**
     * Gets the Object Idendifier corresponding to the extended request type.
     * This is the <b>requestName</b> portion of the ext. req. PDU.
     *
     * @return the dotted-decimal representation as a String of the OID
     */
    public String getOid()
    {
        return oid;
    }


    /**
     * Sets the Object Idendifier corresponding to the extended request type.
     *
     * @param oid the dotted-decimal representation as a String of the OID
     */
    public void setOid( String oid )
    {
        lockCheck( "Attempt to alter OID of locked ExtendedRequest!" );
        this.oid = oid;
    }


    /**
     * Gets the extended request's <b>requestValue</b> portion of the PDU.  The
     * form of the data is request specific and is determined by the extended
     * request OID.
     *
     * @return byte array of data
     */
    public byte [] getPayload()
    {
        return payload;
    }


    /**
     * Sets the extended request's <b>requestValue</b> portion of the PDU.
     *
     * @param payload byte array of data encapsulating ext. req. parameters
     */
    public void setPayload( byte [] payload )
    {
        lockCheck( "Attempt to alter payload of locked ExtendedRequest!" );
        this.payload = payload;
    }


    // ------------------------------------------------------------------------
    // SingleReplyRequest Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the protocol response message type for this request which produces
     * at least one response.
     *
     * @return the message type of the response.
     */
    public MessageTypeEnum getResponseType()
    {
        return RESP_TYPE;
    }


    /**
     * Checks to see if an object equals this ExtendedRequest.
     *
     * @param obj the object to be checked for equality
     * @return true if the obj equals this ExtendedRequest, false otherwise
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

        ExtendedRequest req = ( ExtendedRequest ) obj;
        if ( oid != null && req.getOid() == null )
        {
            return false;
        }

        if ( oid == null && req.getOid() != null )
        {
            return false;
        }

        if ( oid != null && req.getOid() != null )
        {
            if ( ! oid.equals( req.getOid() ) )
            {
                return false;
            }
        }

        if ( payload != null && req.getPayload() == null )
        {
            return false;
        }

        if ( payload == null && req.getPayload() != null )
        {
            return false;
        }

        if ( payload != null && req.getPayload() != null )
        {
            if ( ! ArrayUtils.isEquals( payload, req.getPayload() ) )
            {
                return false;
            }
        }

        return true;
    }
}
