
package com.atomikos.vendor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * Copyright &copy; 2003, Atomikos. All rights reserved.
 *
 * A JAR file utility class.
 */

public class JarArchive 
{



	private File stageFolder_;
	//where to stage everything
	
	
	/**
	 * Create a new instance.
	 * @param stageFolder Where to place temporary files.
	 * @exception IOException If stageFolder is not a directory.
	 */
	
	public JarArchive ( File stageFolder ) 
	throws IOException
	{
		super();
		
		if ( ! stageFolder.isDirectory() ) throw new IOException ( "Not a directory");
		if ( stageFolder.exists() ) stageFolder.delete();
		
		if ( ! stageFolder.exists() ) stageFolder.mkdir();
		stageFolder_ = stageFolder;
	}
	
	
	
	/**
	 * Get the stage folder.
	 * @return File The folder.
	 */
	
	public File getStageFolder()
	{
		return stageFolder_;
	}
	
	protected void add ( File file , File destination )
	throws IOException
	{
		if ( file.getName().startsWith( "." )) return;
		//ignore hidden files
		
		boolean created = false;
		System.out.println ( "Adding file: " + IOHelper.getDifferencePath ( stageFolder_ , file) );
		File target = new File ( destination , IOHelper.getDifferencePath ( stageFolder_ , file) );
		//File target = new File ( destination , file.getName() );
		if ( ! target.exists() ) {
			created = IOHelper.createPathTo ( target , file.isDirectory() );
			if ( !created )
				throw new IOException ( 
				"Could not create copy: " + file.getName() );
		}
		//copy byte by byte, unless it is a directory
		if ( file.isDirectory() ) {
			//call add for each of the entries in the directory
			File[] contents = file.listFiles();
			for ( int i = 0 ; i < contents.length ; i++ ) {
				add ( contents[i] , target );
			}
		}
		else {
			System.out.println ( "Copying: " + file.getPath() + " to " + target.getPath() );
			//FILE -> copy byte by byte
			FileInputStream in = new FileInputStream ( file );
			FileOutputStream out = new FileOutputStream ( target );
			IOHelper.copyBytes ( in , out );
			in.close();
			out.close();
		}
	}
	
	/**
	 * Add a file to the stage folder.
	 * @param file The file to add.
	 * @throws IOException On IO errors.
	 */
	
	public void add ( File file )
	throws IOException
	{
		if ( file.getName().startsWith( "." )) return;
		//ignore hidden files
		add ( file , stageFolder_ );
	}
	
	/**
	 *Add a file to the META-INF directory for the jar. 
	 * @param file The file to add. Only the file name will be taken into
	 * account; any parent folders are ignored.
	 * @throws IOException On error.
	 */
	
	public void addToMetaInf ( File file )
	throws IOException
	{
		File metadir = new File ( stageFolder_ , "META-INF");
		metadir.mkdir();
		File target = new File ( metadir , file.getName() );
		target.createNewFile();
		target.deleteOnExit();
		FileInputStream in = new FileInputStream ( file );
		FileOutputStream out = new FileOutputStream ( target );
		IOHelper.copyBytes ( in , out );
		in.close();
		out.close();
	
		
	}
	
	/**
	 * Add a jar file to the stage folder. This file will first
	 * be extracted.
	 * 
	 * @param jarFile The jar file to add.
	 * @throws IOException If the file is not a jar file, or
	 * if it could not be added.
	 */
	
	public void addJar ( File jarFile )
	throws IOException
	{
		//first extract jar contents
		JarFile jar = new JarFile ( jarFile );
		Enumeration entries = jar.entries();
		while ( entries.hasMoreElements() ) {
			JarEntry entry = ( JarEntry ) entries.nextElement();
			File target = new File ( stageFolder_, entry.getName() );
			if ( entry.isDirectory() ) target.mkdir();
			else {
				target.createNewFile();
				InputStream in = jar.getInputStream ( entry );
				OutputStream out = new FileOutputStream ( target );
				IOHelper.copyBytes ( in , out );
				in.close();
				out.close();
			}
			target.deleteOnExit();
		} 
	
		//@todo Handle MANIFEST???
	
	}
		
		
	
	/**
	 * Create a new jar file containing everything that 
	 * was added so far.
	 * 
	 * @param name The name of the archive file.
	 * @param destinationFolder The destination for the archive file.
	 * @return File The resulting file.
	 * @throws IOException If the archive could not be created.
	 */
	
	public File createJar ( String name , File destinationFolder )
	throws IOException
	{
		//create jar with given name in given directory
		File jar = new File ( destinationFolder , name );
		if ( !jar.exists() ) jar.createNewFile();
		JarOutputStream out = new JarOutputStream ( new FileOutputStream ( jar ) );
		File[] contents = stageFolder_.listFiles();
		for ( int i = 0 ; i < contents.length ; i++ ) {
			addEntryToJar ( out , contents[i] );
		}
		
		out.close();
		return jar;
	}

	protected void addEntryToJar ( JarOutputStream jarOut , File entry )
	throws IOException
	{
		
		//System.out.println ( "Adding file to jar: " + entry.getPath() );
		JarEntry jarEntry = new JarEntry ( 
			IOHelper.getRelativePath ( stageFolder_ , entry ) );
		
		if ( entry.isDirectory() ) {
			File[] contents = entry.listFiles();
			for ( int i = 0 ; i < contents.length ; i++ ) {
				File next = contents[i];
				addEntryToJar ( jarOut , next );			
			}
		}
		else {
			jarOut.putNextEntry ( jarEntry );
			FileInputStream in = new FileInputStream ( entry );
			IOHelper.copyBytes ( in , jarOut );
			in.close();
		}
	}
}
