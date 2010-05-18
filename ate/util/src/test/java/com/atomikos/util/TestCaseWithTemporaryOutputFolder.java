package com.atomikos.util;

import java.io.File;

import junit.framework.TestCase;

public abstract class TestCaseWithTemporaryOutputFolder extends TestCase 
{

    private static final String DEFAULT_OUTPUT_DIR = "./target/testoutput";
	private static int globalcounter = 0;

	private static synchronized int getNextCounterValue() {
	    globalcounter++;
	    return globalcounter;
	}

	private boolean failed;
	private String outputDir;
	private int count;

	/**
     * Creates a new instance with the default 
     * output dir for temp files.
     * @param name
     */
    protected TestCaseWithTemporaryOutputFolder ( String name )
    {
        this ( name, DEFAULT_OUTPUT_DIR );
    }
    
    /**
     * Creates a new instance with a given output dir.
     * @param name
     * @param tempDir
     */
    protected TestCaseWithTemporaryOutputFolder ( String name , String tempDir )
    {
        super ( name );
        failed = false;
        outputDir = tempDir;
        count = getNextCounterValue();
    }
    /**
     * Creates the temporary output directory.
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
    {
    	File outputDir = new File ( getTemporaryOutputDir() );
        if ( ! outputDir.exists() ) outputDir.mkdir();
        else {
            deleteFiles();
            outputDir.mkdir();
        }
        
    }
    
    /**
     * Cleans the temporary output files.
     * Does nothing if failed is set to true.
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown()
    {
        if ( !failed ) {
            //keep output for failed tests
            deleteFiles();
        }
        
    }

	/**
	 * Sets the failure mode. 
	 * @param failed If true, then the 
	 * temporary output files will NOT be deleted
	 * but rather kept for inspection. In that case,
	 * any subsequent tests may also fail due to file
	 * conflicts.
	 */
	protected void setFailed(boolean failed) {
	    this.failed = failed;
	}

	/**
	 * Preferred method for failing a test.
	 * This method sets the failure mode and
	 * includes the output folder in the message.
	 * @param msg The message for the failure.
	 * 
	 */
	protected void failTest(String msg) {
	    setFailed ( true );
	    msg = "[ see " + getTemporaryOutputDir() + " ]" + msg; 
	    super.fail ( msg );
	}

	/**
	 * Gets the path to the temporary output dir.
	 * @return
	 */
	protected String getTemporaryOutputDir() {
	    return outputDir + "_" + count;
	}
	
	protected String getTemporaryOutputDirAsAbsolutePath() {
		return new File ( getTemporaryOutputDir() ).getAbsolutePath();
	}

	private void deleteFiles() {
	    File outputDir = new File ( getTemporaryOutputDir() );
	    File[] files = outputDir.listFiles();
	    if ( files == null ) return;
	    for ( int i = 0 ; i < files.length ; i++ ) {
	        if ( files[i] != null ) files[i].delete();
	    }
	    if ( outputDir != null ) outputDir.delete();
	}
}
