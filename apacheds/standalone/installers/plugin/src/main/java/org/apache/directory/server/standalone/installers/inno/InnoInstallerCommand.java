/*
 *   Copyright 2006 The Apache Software Foundation
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
package org.apache.directory.server.standalone.installers.inno;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.directory.server.standalone.installers.MojoCommand;
import org.apache.directory.server.standalone.installers.MojoHelperUtils;
import org.apache.directory.server.standalone.installers.ServiceInstallersMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Touch;
import org.codehaus.plexus.util.Os;


/**
 * The IzPack installer command.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class InnoInstallerCommand implements MojoCommand
{
    private final Properties filterProperties = new Properties( System.getProperties() );
    private final ServiceInstallersMojo mymojo;
    private final InnoTarget target;
    private final File innoConfigurationFile;
    private final Log log;
    
    private File innoCompiler;
    
    
    public InnoInstallerCommand( ServiceInstallersMojo mymojo, InnoTarget target )
    {
        this.mymojo = mymojo;
        this.target = target;
        this.log = mymojo.getLog();
        File imagesDir = target.getLayout().getBaseDirectory().getParentFile();
        innoConfigurationFile = new File( imagesDir, target.getId() + ".iss" );
        initializeFiltering();
    }
    
    
    /**
     * Performs the following:
     * <ol>
     *   <li>Bail if target is not for windows or current machine is not windows (no inno compiler)</li>
     *   <li>Filter and copy project supplied inno file into place if it has been specified and exists</li>
     *   <li>If no inno file exists filter and deposite into place bundled inno template</li>
     *   <li>Copy over the procrun executables with proper names for the application</li>
     *   <li>Bail if we cannot find the inno compiler executable</li>
     *   <li>Execute inno compiler it on the inno file</li>
     * </ol> 
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        // -------------------------------------------------------------------
        // Do some error checking first
        // -------------------------------------------------------------------
        
        if ( ! target.getOsFamily().equals( "windows" ) )
        {
            throw new MojoFailureException( "Inno installers can only be targeted for windows platforms!" );
        }
        
        if ( ! Os.isFamily( "windows" ) ) 
        {
            log.warn( "Inno target " + target.getId() + " cannot be built on a non-windows machine!" );
        }
        
        if ( ! target.getInnoCompiler().exists() )
        {
            throw new MojoFailureException( "Cannot find Inno compiler: " + target.getInnoCompiler() );
        }
        
        // -------------------------------------------------------------------
        // Copy over the procrun binaries with new app names
        // -------------------------------------------------------------------
        
        File prunsrvFile = new File( target.getLayout().getBinDirectory(), mymojo.getApplicationName() + ".exe" );
        InputStream prunsrvIn = getClass().getResourceAsStream( "../prunsrv.exe" );
        
        try
        {
            MojoHelperUtils.copyBinaryFile( prunsrvIn, prunsrvFile );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Failed to extract bundled prunsrv.exe " + 
                getClass().getResource( "../prunsrv.exe" ) + " into image destingation " + target.getLayout().getBinDirectory() );
        }
    }


    private void initializeFiltering() 
    {
        filterProperties.putAll( mymojo.getProject().getProperties() );
        filterProperties.put( "app" , mymojo.getApplicationName() );
        
        char firstChar = mymojo.getApplicationName().charAt( 0 );
        firstChar = Character.toUpperCase( firstChar );
        filterProperties.put( "app.displayname", firstChar + mymojo.getApplicationName().substring( 1 ) );

        if ( mymojo.getApplicationVersion() != null )
        {
            filterProperties.put( "app.version", mymojo.getApplicationVersion() );
        }
        
        if ( mymojo.getApplicationDescription() != null )
        {
            filterProperties.put( "app.init.message", mymojo.getApplicationDescription() );
        }

        // -------------------------------------------------------------------
        // WARNING: hard code values just to for testing
        // -------------------------------------------------------------------
        
        filterProperties.put( "app.author" , target.getApplicationAuthor() );
        filterProperties.put( "app.email" , target.getApplicationEmail() );
        filterProperties.put( "app.url" , target.getApplicationUrl() );
        filterProperties.put( "app.java.version" , target.getApplicationJavaVersion() );
        filterProperties.put( "app.license" , target.getLayout().getLicenseFile().getPath() );

        if ( ! target.getLayout().getReadmeFile().exists() )
        {
            touchFile( target.getLayout().getReadmeFile() );
        }
        filterProperties.put( "app.readme" , target.getLayout().getReadmeFile().getPath() );
        filterProperties.put( "app.icon" , target.getLayout().getLogoIconFile().getPath() );
        filterProperties.put( "image.basedir", target.getLayout().getBaseDirectory().getPath() );
    }
    
    
    static void touchFile( File file )
    {
        Touch touch = new Touch();
        touch.setProject( new Project() );
        touch.setFile( file );
        touch.execute();
    }
}
