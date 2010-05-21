package com.atomikos.vendor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * A helper class for common IO tasks.
 */
public class IOHelper
{

		/**
	 * Copy every byte from the source to the output.
	 * @param in The source stream.
	 * @param out The output stream.
	 * @throws IOException On IO errors.
	 */
	
	public static void copyBytes ( InputStream in , OutputStream out )
	throws IOException
	{
		
		byte[] buffer = new byte[1024];
		int read = in.read ( buffer );
		while ( read >= 0 ) {
			out.write ( buffer , 0 , read );
			read = in.read ( buffer );
		}
	}
	
	/**
	 * Gets the relative path of a file wrt one of its parent directories.
	 * 
	 * @param fromFolder The parent directory
	 * @param toFile The file
	 * @return String The relative path
	 * @throws IOException On error, for instance if toFile is not in fromFolder
	 */
	
	public static String getRelativePath ( File fromFolder , File toFile )
	throws IOException
	{
		
		String basePath = fromFolder.getAbsolutePath();
		String filePath = toFile.getAbsolutePath();
		if ( ! filePath.startsWith ( basePath )) 
			throw new IOException ( 
			"The specified file is not within the folder.");
		return filePath.substring ( basePath.length() + 1 );
	
	}
	
	
	/**
	 * Get the difference path of a file wrt a folder. 
	 * @param fromFolder The folder.
	 * @param toFile The file. This file should be contained in the parent
	 * of fromFolder!
	 * @return String The path from the parent of fromFolder to toFile.
	 * @throws IOException
	 */
	
	public static String getDifferencePath ( File fromFolder , File toFile )
	throws IOException
	{
		String commonParentPath = fromFolder.getParentFile().getAbsolutePath();
		String filePath = toFile.getAbsolutePath();
		if ( ! filePath.startsWith ( commonParentPath )) 
			throw new IOException (
			"The file should be contained in the parent directory of the stage folder.");
		
		return filePath.substring ( commonParentPath.length() +1 );
		
	}
	
	/**
	 * Recursively create the path to the given file if it does not exist.
	 * @param file The file to create. All nonexistent parent folders will also
	 * be created.
	 * @param isDirectory True iff the file should be a directory
	 * @return boolean True if success.
	 * @throws IOException
	 */
	
	public static boolean createPathTo ( File file , boolean isDirectory )
	throws IOException
	{
		boolean ret = false;
		File parent = file.getParentFile();
		if ( parent != null && ! parent.exists() ) createPathTo ( parent , true );
		if ( isDirectory) ret = file.mkdir();
		else ret = file.createNewFile();
		//removed deleteOnExit: not OK for log dirs in TM init
		//file.deleteOnExit();
		//System.out.println ( "createPathTo ( " + file + " , " + isDirectory + " )");
		return ret;
	}
	
	/**
	 * Recursively delete the contents of the given folder.
	 * @param folder The folder to delete.
	 * @return boolean True if success.
	 * @throws IOException
	 */
	
	public static boolean deleteContents ( File folder )
	throws IOException
	{
		boolean deleted = true;
		if ( ! folder.isDirectory() ) 
			throw new IOException ( "Not a directory");
		
		File[] contents = folder.listFiles();
		for ( int i = 0 ; i < contents.length ; i++ ) {
			if ( contents[i].isDirectory() ) {
				if ( ! deleteContents ( contents[i] )) deleted = false;
			}
			else if ( ! contents[i].delete() ) deleted = false;
		}
		return deleted;
	}

}
