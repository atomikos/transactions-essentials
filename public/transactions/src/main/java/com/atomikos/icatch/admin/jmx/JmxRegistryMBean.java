/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import javax.management.MBeanRegistration;

/**
 * A registry MBean interface. This interface allows to get the MBeanServer (if
 * desired) from classes that are not themselves MBeans.
 */
public interface JmxRegistryMBean extends MBeanRegistration
{

}
