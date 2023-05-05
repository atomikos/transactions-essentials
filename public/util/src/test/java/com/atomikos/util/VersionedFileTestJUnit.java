/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

public class VersionedFileTestJUnit extends TestCase {

	private static final String SUFFIX = ".dat";
	private static final String BASEDIR = "." + File.separatorChar;
	
	private VersionedFile file;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		file = new VersionedFile ( BASEDIR , getBaseName() , SUFFIX );
	}

	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	private String getBaseName() 
	{
		return getName();
	}
	
	private String getBaseUrl() 
	{
		return BASEDIR + getBaseName();
	}
	
	public void testGetBaseUrl() 
	{
		assertEquals ( getBaseUrl() , file.getBaseUrl() );
	}
	
	public void testAppendFileSeparator() {
		file = new VersionedFile ( "." , getBaseName() , SUFFIX );
		testGetBaseUrl();
	}
	public void testGetBaseName() 
	{
		assertEquals ( getBaseName() , file.getBaseName() );
	}
	
	
	public void testReadingNonExistingLogBaseDirShouldFailWithMeaningFulException() throws Exception {
		String nonExistingBaseDir = "nonExistingBaseDir";
		try {
			file = new VersionedFile ( nonExistingBaseDir , getBaseName() , SUFFIX );
			file.openLastValidVersionForReading();
		} catch (Exception e) {
			String message = e.getMessage();
			assertTrue(message.contains(nonExistingBaseDir));
		}
	}
	public void testWritingNonExistingLogBaseDirShouldFailWithMeaningFulException() throws Exception {
		String nonExistingBaseDir = "nonExistingBaseDir";
		try {
			file = new VersionedFile ( nonExistingBaseDir , getBaseName() , SUFFIX );
			file.openNewVersionForNioWriting();
		} catch (Exception e) {
			String message = e.getMessage();
			assertTrue(message.contains(nonExistingBaseDir));
		}
	}
	public void testGetSuffix() 
	{
		assertEquals ( SUFFIX , file.getSuffix() );
	}
	
	public void testGetBaseDir() 
	{
		assertEquals ( BASEDIR , file.getBaseDir() );
	}
	
	public void testBasicUsage() throws IOException 
	{
		int value = 0;
		//first recover the file's content
		InputStream is;
		DataInputStream dis;
		try {
			is = file.openLastValidVersionForReading();
			dis = new DataInputStream ( is );
			if ( dis.available() > 0 ) value = dis.readInt();
			dis.close();
		} catch ( FileNotFoundException noPreviousVersionExists ) {
		
		}
		
		int previousValue = value;
		
		//increment the value read
		value++;
		
		//now, start writing and save the incremented value
		//if anything fails here then the file is guaranteed to have the
		//original (recovered) value still available on disk as a previous version
		OutputStream os = file.openNewVersionForWriting();
		DataOutputStream dos = new DataOutputStream ( os );
		dos.writeInt ( value );
		dos.close();
		
		//now we are sure that sufficient information has been written:
		//discard any backup version - which makes the newer version current
		file.discardBackupVersion();
		file.close();
		
		//we must now be able to read the newer version
		is = file.openLastValidVersionForReading();
		dis = new DataInputStream ( is );
		if ( dis.available() > 0 ) value = dis.readInt();
		dis.close();
		assertEquals ( previousValue + 1 , value );
		file.close();		
	}
	
	
	
	public void testCallingOpenNewVersionForWritingTwiceThrowsException() throws IOException 
	{
		file.openNewVersionForWriting();
		try {
			file.openNewVersionForWriting();
			fail ( "Exception expected" );
		} catch ( IllegalStateException ok ) {
			
		}
	}
	
	public void testDiscardBackupVersionFailsIfNoNewVersion() throws IOException 
	{
		try {
			file.discardBackupVersion();
			fail ( "Exception expected" );
		} catch ( IllegalStateException ok ) {}
	}
	
	public void testCallingOpenLastValidVersionForReadingFailsIfWriting() throws IOException 
	{
		file.openNewVersionForWriting();
		try {
			file.openLastValidVersionForReading();
			fail ( "Exception expected" );
		} catch ( IllegalStateException ok ) {
			
		}
	}
	
	public void testNotCallingDiscardBackupVersionReturnsOldDataOnReadingAgain() throws IOException 
	{
		int value = 0;
		//first recover the file's content
		InputStream is;
		DataInputStream dis;
		try {
			is = file.openLastValidVersionForReading();
			dis = new DataInputStream ( is );
			if ( dis.available() > 0 ) value = dis.readInt();
			dis.close();
		} catch ( FileNotFoundException noPreviousVersionExists ) {
		
		}
		
		int previousValue = value;
		
		//increment the value read
		value++;
		
		//now, start writing and save the incremented value
		//if anything fails here then the file is guaranteed to have the
		//original (recovered) value still available on disk as a previous version
		OutputStream os = file.openNewVersionForWriting();
		DataOutputStream dos = new DataOutputStream ( os );
		dos.writeInt ( value );
		dos.close();
		
		//now we are sure that sufficient information has been written:
		//discard any backup version - which makes the newer version current
		file.discardBackupVersion();
		file.close();
		
		//we must now be able to read the newer version
		is = file.openLastValidVersionForReading();
		dis = new DataInputStream ( is );
		if ( dis.available() > 0 ) value = dis.readInt();
		dis.close();
		assertEquals ( previousValue + 1 , value );
		file.close();	
		
		//increment the value read again
		previousValue = value;
		value++;
		
		os = file.openNewVersionForWriting();
		dos = new DataOutputStream ( os );
		dos.writeInt ( value );
		dos.close();
		//close and don't discard the backup version
		file.close();
		
		//we must now be able to read the old version
		//since the backup version was never discarded
		//even though a new version was written
		is = file.openLastValidVersionForReading();
		dis = new DataInputStream ( is );
		if ( dis.available() > 0 ) value = dis.readInt();
		dis.close();
		assertEquals ( previousValue , value );
		file.close();
	}

}
