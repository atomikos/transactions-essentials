/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.persistence.dataserializable;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Vector;

import com.atomikos.icatch.DataSerializable;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.Recoverable;
import com.atomikos.persistence.imp.AbstractLogStream;
import com.atomikos.persistence.imp.SystemLogImage;

/**
 * A file implementation of a LogStream.
 */

public class FileLogStream extends AbstractLogStream implements LogStream {
	


	public FileLogStream(String baseDir, String baseName) throws IOException {
		super(baseDir,baseName);
	}

	public Vector<Recoverable> recover() throws LogException {
		
		if (corrupt_)
			throw new LogException("Instance might be corrupted");

		
		Vector<Recoverable> ret = new Vector<Recoverable>();

		try {
			
			FileInputStream f = file_.openLastValidVersionForReading();
			

			int count = 0;
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("Starting read of logfile " + file_.getCurrentVersionFileName());
			}
			
			while (f.available()>0) {
				// if crashed, then unproper closing might cause endless blocking!
				// therefore, we check if avaible first.
				count++;
				
				SystemLogImage systemLogImage = new SystemLogImage();

				systemLogImage.readData(new DataInputStream(f));
				ret.addElement(systemLogImage);
				if (count % 10 == 0) {
					LOGGER.logInfo(".");
				}

			}
			LOGGER.logInfo("Done read of logfile");

		} catch (java.io.EOFException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (StreamCorruptedException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (ObjectStreamException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} catch (Exception e) {
			String msg = "Error in recover";
			LOGGER.logWarning(msg, e);
			throw new LogException(msg, e);
		}

		return ret;
	}

	public void writeCheckpoint(Enumeration elements) throws LogException {
		
		synchronized (file_) {
			// first, make sure that any pending output stream handles
			// in the client are invalidated
			closeOutput();

			try {
				// open the new output file
				// NOTE: after restart, any previous and failed checkpoint files
				// will be overwritten here. That is perfectly OK.
				output_ = file_.openNewVersionForWriting();
				final DataByteArrayOutputStream dataByteArrayOutputStream = new DataByteArrayOutputStream();
				while (elements != null && elements.hasMoreElements()) {
					DataSerializable next = (DataSerializable) elements.nextElement();
					dataByteArrayOutputStream.restart();
					next.writeData(dataByteArrayOutputStream);
					output_.write(dataByteArrayOutputStream.getContent());
				}

				output_.getFD().sync();
				// NOTE: we do NOT close the object output, since the client
				// will probably want to write more!
				// Thus, we return the open stream to the client.
				// Any closing will be done later, during cleanup if necessary.

				if (simulateCrash_) {
					corrupt_ = true;
					throw new LogException("Old file could not be deleted");
				}

				try {
					file_.discardBackupVersion();
				} catch (IOException errorOnDelete) {
					corrupt_ = true;
					// should restart
					throw new LogException("Old file could not be deleted");
				}
			} catch (Exception e) {
				throw new LogException("Error during checkpointing", e);
			}

		}
		
	}

	public void flushObject(Object o, boolean shouldSync) throws LogException {
		try {
			final DataByteArrayOutputStream dataByteArrayOutputStream = new DataByteArrayOutputStream();
			DataSerializable oo = (DataSerializable) o;
			oo.writeData(dataByteArrayOutputStream);
			dataByteArrayOutputStream.close();
			// take care of checkpoint...
			synchronized (file_) {
				if(output_!=null){
					output_.write(dataByteArrayOutputStream.getContent());	
				}
			}
			if (shouldSync && output_!=null) 	output_.getFD().sync();
		} catch (IOException e) {

			throw new LogException(e.getMessage(), e);
		}
	}
}
