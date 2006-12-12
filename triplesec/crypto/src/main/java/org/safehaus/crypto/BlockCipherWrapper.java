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
package org.safehaus.crypto;


import java.io.UnsupportedEncodingException;


/**
 * Wraps a BlockCipher to simplify its use in encrypting/decrypting variable
 * length inputs.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BlockCipherWrapper
{
    /** the underlying BlockCipher engine */
    private final Class engineClass;


    /**
     * Creates a BlockCipherWrapper for an underlying BlockCipher.
     *
     * @param engineClass the class of the engine to wrap
     */
    public BlockCipherWrapper( Class engineClass )
    {
        this.engineClass = engineClass;
    }


    /**
     * Encrypts a byte array using underlying engine with a password String.
     * To generate the key bytes the UTF-8 encoding is presumed for the password.
     * The generated bytes may be tail padded to reach a multiple of the block
     * size.
     *
     * @param password the string representing the users password
     * @param input the unpadded data to be encrypted
     * @return the encrypted data padded to the nearest multiple of the block size
     * @throws java.io.UnsupportedEncodingException if the UTF-8 encoding is not supported
     */
    public byte[] encrypt( String password, byte[] input ) throws UnsupportedEncodingException
    {
        KeyParameter key = generateKey( password );
        return encrypt( key, input );
    }


    /**
     * Encrypts the input with the supplied key.
     *
     * @param key the key to use for encrypting the input
     * @param input the unpadded data to encrypt
     * @return the encrypted data padded to the nearest multiple of the block size
     */
    public byte[] encrypt( KeyParameter key, byte[] input )
    {
        BlockCipher engine;

        try
        {
            engine = ( BlockCipher ) engineClass.newInstance();
        }
        catch ( Exception e)
        {
            throw new RuntimeCryptoException( e.getMessage() );
        }

        engine.init( true, key );
        int oriInputLength = input.length;
        int paddedInputLength = ( input.length/engine.getBlockSize() + 1 ) * engine.getBlockSize();
        byte[] paddedInput = new byte[paddedInputLength];
        System.arraycopy( input, 0, paddedInput, 0, oriInputLength );
        byte[] paddedOutput = new byte[paddedInputLength];

        int pos = 0;
        while ( pos < paddedInputLength )
        {
            pos += engine.processBlock( paddedInput, pos, paddedOutput, pos );
        }

        return paddedOutput;
    }


    /**
     * Decrypts an encrypted input with the supplied key.
     *
     * @param password the string representing the users password
     * @param input the unpadded data to be encrypted
     * @return the decrypted data padded to the nearest multiple of the block size
     * @throws UnsupportedEncodingException if the UTF-8 encoding is not supported
     */
    public byte[] decrypt( String password, byte[] input ) throws UnsupportedEncodingException
    {
        KeyParameter key = generateKey( password );
        return decrypt( key, input );
    }


    /**
     * Decrypts the input with the supplied key.
     *
     * @param key the key to use for decrypting the input
     * @param input the padded cipher data to decrypt
     * @return the decrypted data padded to the nearest multiple of the block size
     */
    public byte[] decrypt( KeyParameter key, byte[] input )
    {
        BlockCipher engine;

        try
        {
            engine = ( BlockCipher ) engineClass.newInstance();
        }
        catch ( Exception e)
        {
            throw new RuntimeCryptoException( e.getMessage() );
        }

        engine.init( false, key );
        int oriInputLength = input.length;
        int adjInputLength = ( input.length/engine.getBlockSize() + 1 ) * engine.getBlockSize();
        byte[] adjInput = new byte[adjInputLength];
        System.arraycopy( input, 0, adjInput, 0, oriInputLength );
        byte[] adjOutput = new byte[adjInputLength];

        int pos = 0;
        while ( pos < adjInputLength )
        {
            pos += engine.processBlock( adjInput, pos, adjOutput, pos );
        }

        byte[] output = new byte[oriInputLength];
        System.arraycopy( adjOutput, 0, output, 0, oriInputLength );
        return output;
    }


    public KeyParameter generateKey( String password ) throws UnsupportedEncodingException
    {
    	// commented out a bunch of stuff that caused failures when we obfuscated the app
    	
        byte[] passwordBytes = password.getBytes( "UTF-8" );
//        int bits = passwordBytes.length << 3;
        byte[] key;

//        if ( engineClass.getName().equals( "org.safehaus.crypto.AESEngine" ) )
//        {
//            if ( bits < 128 )
//            {
//                key = new byte[16];
//                System.arraycopy( passwordBytes, 0, key, 0, passwordBytes.length );
//            }
//            else if ( 128 < bits && bits < 192 )
//            {
//                key = new byte[24];
//                System.arraycopy( passwordBytes, 0, key, 0, passwordBytes.length );
//            }
//            else
//            {
//                key = new byte[32];
//                System.arraycopy( passwordBytes, 0, key, 0, passwordBytes.length );
//            }
//        }
//        else if ( engineClass.getName().equals( "org.safehaus.crypto.DESEngine" ) )
//        {
            key = new byte[8];

            if ( 8 > passwordBytes.length )
            {
                System.arraycopy( passwordBytes, 0, key, 0, passwordBytes.length );
            }
            else
            {
                System.arraycopy( passwordBytes, 0, key, 0, 8 );
            }
//        }
//        else
//        {
//            throw new RuntimeCryptoException( "unrecognized engine class cannot generate key for it" );
//        }

        return new KeyParameter( key );
    }
}
