/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.event;

import java.util.Properties;

import com.atomikos.icatch.event.Event;

public class XAResourceDetectedEvent  extends Event {

	private static final long serialVersionUID = 7274092099878605813L;

	private final String xaClassName;
	
	private final Properties xaProperties;
	
	private final ResourceType resourceType;
	
	
	public XAResourceDetectedEvent(String xaClassName, Properties xaProperties,
			ResourceType resourceType) {
		super();
		this.xaClassName = xaClassName;
		this.xaProperties = xaProperties;
		this.resourceType = resourceType;
	}


	public ResourceType getResourceType() {
		return resourceType;
	}
	
	public String getXaClassName() {
		return xaClassName;
	}
	
	public Properties getXaProperties() {
		return xaProperties;
	}
	
	
	public enum ResourceType {
		JDBC,JMS;
	}


	@Override
	public String toString() {
		return "XAResourceDetectedEvent [xaClassName=" + xaClassName
				+ ", xaProperties=" + xaProperties + ", resourceType="
				+ resourceType + "]";
	}
	
	
}
