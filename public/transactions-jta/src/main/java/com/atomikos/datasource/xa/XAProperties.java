package com.atomikos.datasource.xa;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 
 * Properties adapter class protecting password from being printed.
 *
 */
public class XAProperties
implements Serializable {

	private static final long serialVersionUID = 1L;

	private Properties xaProperties;

	public XAProperties()
	{
		this( new Properties() );
	}
	
	public XAProperties(Properties xaProperties)
	{
		this.xaProperties = xaProperties;
	}

	public Properties getProperties()
	{
		return xaProperties;
	}

	public String printProperties()
	{
		StringBuffer ret = new StringBuffer();
		if ( xaProperties != null ) {
			Enumeration<?> it = xaProperties.propertyNames();
			ret.append ( "[" );
			boolean first = true;
			while ( it.hasMoreElements() ) {
				String name = ( String ) it.nextElement();
				if ( !"password".equalsIgnoreCase( name ) ) {
					if ( ! first ) ret.append ( "," );
					String value = xaProperties.getProperty( name );
					ret.append ( name ).append ( "=" ).append ( value );
					first = false;
				}
			}
			ret.append ( "]" );
		}
		return ret.toString();
	}

}
