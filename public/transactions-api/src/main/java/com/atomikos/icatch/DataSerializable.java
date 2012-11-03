package com.atomikos.icatch;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface DataSerializable {

	public void writeData(DataOutput out) throws IOException;
	public void readData (DataInput in) throws IOException;
}
