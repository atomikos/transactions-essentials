package com.atomikos.datasource.xa;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Serializable;

import org.junit.Test;

import com.atomikos.util.ClassLoadingHelper;

public class XidTestJUnit {

	@Test
	public void testRecoveredXidIsEqualWithDataOutputInputDesign() throws Exception {
		XID xid = new XID("tid", "resource");
		byte[] data= ClassLoadingHelper.toByteArray((Serializable)xid);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput output = new DataOutputStream(baos);
		output.writeInt(data.length);
		output.write(data);
		DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
		int len = in.readInt();
		data= new byte[len];
		in.readFully(data);
		XID xid2 =(XID)ClassLoadingHelper.toObject(data);
		assertEquals(xid,xid2);
	}

}
