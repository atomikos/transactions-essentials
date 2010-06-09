package com.atomikos.datasource.xa;

import java.io.Serializable;

 /**
  * A test class for serializable instances.
  * Cf case 59238
  *
  */
class SerializableTestXAResource extends TestXAResource implements Serializable 
{


	private static final long serialVersionUID = 1L;

	public SerializableTestXAResource() 
	{
		
	}

}
