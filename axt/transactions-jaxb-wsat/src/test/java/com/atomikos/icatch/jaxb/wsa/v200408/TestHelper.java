package com.atomikos.icatch.jaxb.wsa.v200408;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TestHelper {
	public static Object streamOutAndIn ( Serializable o )
	throws Exception
	{
		Object ret = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream ( bout );
		out.writeObject ( o );
		out.close();
		ByteArrayInputStream bin = new ByteArrayInputStream ( bout.toByteArray() );
		ObjectInputStream in = new ObjectInputStream ( bin );
		ret = in.readObject();
		in.close();
		return ret;
	}
}
