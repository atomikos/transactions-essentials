package com.atomikos.persistence.dataserializable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;


 /**
  * A file with underlying version capabilities to ensure safe overwriting.
  *
  * Unlike regular files, this type of file is safe w.r.t. (over)writing
  * a previous version: a backup version of the original content is kept
  * until the client application explicitly states that a consistent
  * new version has been written.
  *
  */

public class VersionedFile
{

	private String baseDir;
	private String suffix;
	private String baseName;

	//state attributes below

	private long version;
	private RandomAccessFile inputStream;
	private RandomAccessFile outputStream;



	/**
	 * Creates a new instance based on the given name parameters.
	 * The actual complete name(s) of the physical file(s) will be based on a version number
	 * inserted in between, to identify versions.
	 *
	 * @param baseDir The base folder.
	 * @param baseName The base name for of the file path/name.
	 * @param suffix The suffix to append to the complete file name.
	 */
	public VersionedFile ( String baseDir , String baseName , String suffix )
	{
		this.baseDir = baseDir;
		this.suffix = suffix;
		this.baseName = baseName;
		resetVersion();
	}

	private void resetVersion()
	{
		this.version = extractLastValidVersionNumberFromFileNames();
	}

	private long extractLastValidVersionNumberFromFileNames() {
		long version = -1;
        File cd = new File ( getBaseDir() );
        String[] names = cd.list ( new FilenameFilter () {
            public boolean accept ( File dir , String name )
            {
                return (name.startsWith ( getBaseName() ) && name
                        .endsWith ( getSuffix() ));
            }
        } );
        if ( names!= null ) {
        	for ( int i = 0; i < names.length; i++ ) {
        		long sfx = extractVersion ( names[i] );
        		if ( version < 0 || sfx < version )
        			version = sfx;
        	}
        }

        return version;
	}

	private long extractVersion ( String name )
    {
		long ret  = 0;
        int lastpos = name.lastIndexOf ( '.' );
        int startpos = getBaseName().length ();
        String suffix = name.substring ( startpos, lastpos );
        try {

			ret = Long.valueOf( suffix );
		} catch ( NumberFormatException e ) {
			IllegalArgumentException err = new IllegalArgumentException ( "Error extracting version from file: " + name );
			err.initCause ( e );
			throw err;
		}
        return ret;
    }

	private String getBackupVersionFileName()
	{
		return getBaseUrl() + (version - 1) + getSuffix();
	}

	public String getCurrentVersionFileName()
	{
		return getBaseUrl() + version + getSuffix();
	}

	public String getBaseUrl()
	{
		return baseDir + baseName;
	}

	public String getBaseDir()
	{
		return this.baseDir;
	}

	public String getBaseName()
	{
		return this.baseName;
	}

	public String getSuffix()
	{
		return this.suffix;
	}

	/**
	 * Opens the last valid version for reading.
	 *
	 * @return A stream to read the last valid contents
	 * of the file: either the backup version (if present)
	 * or the current (and only) version if no backup is found.
	 *
	 * @throws IllegalStateException If a newer version was opened for writing.
	 * @throws FileNotFoundException If no last version was found.
	 */
	public RandomAccessFile openLastValidVersionForReading()
	throws IllegalStateException, FileNotFoundException
	{
		if ( outputStream != null ) throw new IllegalStateException ( "Already started writing." );
		try {
			inputStream = new RandomAccessFile(getCurrentVersionFileName(),"rw");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * Opens a new version for writing to. Note that
	 * this new version is tentative and cannot be read
	 * by {@link #openLastValidVersionForReading()} until
	 * {@link #discardBackupVersion()} is called.
	 *
	 * @return A stream for writing to.
	 * @throws FileNotFoundException
	 * @throws FileNotFoundException
	 *
	 * @throws IllegalStateException If called more than once
	 * without a close in between.
	 * @throws FileNotFoundException If the file cannot be opened for writing.
	 */
	public RandomAccessFile openNewVersionForWriting() throws FileNotFoundException
	{
		if ( outputStream != null ) throw new IllegalStateException ( "Already writing a new version." );
		version++;
		outputStream = new RandomAccessFile( getCurrentVersionFileName() ,"rw");
		return outputStream;
	}

	/**
	 * Discards the backup version (if any).
	 * After calling this method, the newer version
	 * produced after calling {@link #openNewVersionForWriting()}
	 * becomes valid for reading next time when
	 * {@link #openLastValidVersionForReading()} is called.
	 *
	 * Note: it is the caller's responsibility to make sure that
	 * all new data has been flushed to disk before calling this method!
	 *
	 * @throws IllegalStateException If {@link #openNewVersionForWriting()} has not been called yet.
	 * @throws IOException If the previous version exists but could no be deleted.
	 */
	public void discardBackupVersion() throws IllegalStateException, IOException
	{
		if ( outputStream == null ) throw new IllegalStateException ( "No new version yet!" );
		String fileName = getBackupVersionFileName();
		File temp = new File ( fileName );
        if ( temp.exists() && !temp.delete() ) throw new IOException ( "Failed to delete backup version: " + fileName );

	}

	/**
	 * Closes any open resources and resets the file for reading again.
	 * @throws IOException If the output stream could not be closed.
	 */

	public void close() throws IOException
	{
		resetVersion();
		if ( inputStream != null ) {
			try {
				inputStream.close();
			} catch (IOException e) {
				//don't care and won't happen: closing an input stream
				//does nothing says the JDK javadoc!
			} finally {
				inputStream = null;
			}
		}
		if ( outputStream != null ) {
			try {
				if ( outputStream.getFD().valid() ) outputStream.close();
			} finally {
				outputStream = null;
			}
		}
	}

	public long getSize()
	{
		long res = -1;
		File f = new File ( getCurrentVersionFileName() );
		res = f.length();
		return res;
	}

}