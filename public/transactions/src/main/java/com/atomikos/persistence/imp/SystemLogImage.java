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
package com.atomikos.persistence.imp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.icatch.DataSerializable;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

public class SystemLogImage implements Recoverable, Externalizable, DataSerializable {

	// Force-set the serial version ID to make sure that log
	// data can be read.
	static final long serialVersionUID = 4153546869295179306L;

	protected Recoverable recoverable_ = null;
	protected boolean forgettable_ = false;

	public SystemLogImage() {
		// required for externalizable
		// or for writing the terminating entry on restart.
		forgettable_ = true;
	}

	public SystemLogImage(Recoverable recoverable, boolean forgettable) {
		recoverable_ = recoverable;
		forgettable_ = forgettable;
	}

	private static final String END_OF_LOG_ENTRY = "END_OF_LOG_ENTRY";

	public Object getId() {
		if (recoverable_ == null) // terminating entry
			return END_OF_LOG_ENTRY;
		else
			return recoverable_.getId();
	}

	/**
	 * Test if an image is forgettable. Needed in case of sequential logs, to write a termination image long after an image was flushed.
	 */

	public boolean isForgettable() {
		return forgettable_;
	}

	/**
	 * Get the recoverable. Needed to return the right implementation class to the client!
	 * 
	 * @return Recoverable The wrapped recoverable.
	 */

	public ObjectImage getObjectImage() {
		return recoverable_.getObjectImage();
	}

	public Recoverable getRecoverable() {
		return recoverable_;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		ObjectImage objectimage = null;
		objectimage = (ObjectImage) in.readObject();
		recoverable_ = objectimage.restore();
		forgettable_ = in.readBoolean();

	}

	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectImage img = recoverable_.getObjectImage();
		out.writeObject(img);
		out.writeBoolean(forgettable_);
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeBoolean(forgettable_);
		((DataSerializable) recoverable_).writeData(out);

	}

	public void readData(DataInput in) throws IOException {
		forgettable_ = in.readBoolean();
		recoverable_ = new StateObjectImage();
		((DataSerializable) recoverable_).readData(in);

	}

}
