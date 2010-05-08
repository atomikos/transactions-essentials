package com.atomikos.icatch.admin.jmx;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

/**
 * 
 * 
 * 
 * 
 *
 * A mock implementation of the MBeanServer. This class implements just the 
 * necessary methods for testing the JMX package of Atomikos.
 */

public class TestMBeanServer implements MBeanServer
{

    private Map registeredBeans = new HashMap();
    
    public Object getMBean ( ObjectName name )
    {
    	return registeredBeans.get ( name );
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#instantiate(java.lang.String)
     */
    public Object instantiate(String arg0)
        throws ReflectionException, MBeanException
    {
        throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#instantiate(java.lang.String, javax.management.ObjectName)
     */
    public Object instantiate(String arg0, ObjectName arg1)
        throws ReflectionException, MBeanException, InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#instantiate(java.lang.String, java.lang.Object[], java.lang.String[])
     */
    public Object instantiate(String arg0, Object[] arg1, String[] arg2)
        throws ReflectionException, MBeanException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#instantiate(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
     */
    public Object instantiate(
        String arg0,
        ObjectName arg1,
        Object[] arg2,
        String[] arg3)
        throws ReflectionException, MBeanException, InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#createMBean(java.lang.String, javax.management.ObjectName)
     */
    public ObjectInstance createMBean(String arg0, ObjectName arg1)
        throws
            ReflectionException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException,
            MBeanException,
            NotCompliantMBeanException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
     */
    public ObjectInstance createMBean(
        String arg0,
        ObjectName arg1,
        ObjectName arg2)
        throws
            ReflectionException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException,
            MBeanException,
            NotCompliantMBeanException,
            InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
     */
    public ObjectInstance createMBean(
        String arg0,
        ObjectName arg1,
        Object[] arg2,
        String[] arg3)
        throws
            ReflectionException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException,
            MBeanException,
            NotCompliantMBeanException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
     */
    public ObjectInstance createMBean(
        String arg0,
        ObjectName arg1,
        ObjectName arg2,
        Object[] arg3,
        String[] arg4)
        throws
            ReflectionException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException,
            MBeanException,
            NotCompliantMBeanException,
            InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#registerMBean(java.lang.Object, javax.management.ObjectName)
     */
    public ObjectInstance registerMBean(Object arg0, ObjectName arg1)
        throws
            InstanceAlreadyExistsException,
            MBeanRegistrationException,
            NotCompliantMBeanException
    {
		
		if ( arg0 instanceof MBeanRegistration ) {
			MBeanRegistration reg = ( MBeanRegistration ) arg0;
			try {
				reg.preRegister( this , arg1 );
			}
			catch ( Exception e ) {
				throw new MBeanRegistrationException ( e );
			}
		}
		registeredBeans.put ( arg1 , arg0 );
		if ( arg0 instanceof MBeanRegistration ) {
					MBeanRegistration reg = ( MBeanRegistration ) arg0;
					try {
						reg.postRegister( new Boolean ( true ) );
					}
					catch ( Exception e ) {
						throw new MBeanRegistrationException ( e );
					}
				}
		return null;
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#unregisterMBean(javax.management.ObjectName)
     */
    public void unregisterMBean(ObjectName arg0)
        throws InstanceNotFoundException, MBeanRegistrationException
    {
		if ( arg0 instanceof MBeanRegistration ) {
			MBeanRegistration reg = ( MBeanRegistration ) arg0;
			try {
				reg.preDeregister();
			}
			catch ( Exception e ) {
				throw new MBeanRegistrationException ( e );
			}
		}
		registeredBeans.remove ( arg0 );
		if ( arg0 instanceof MBeanRegistration ) {
			MBeanRegistration reg = ( MBeanRegistration ) arg0;
			try {
				reg.postDeregister();
			}
			catch ( Exception e ) {
				throw new MBeanRegistrationException ( e );
			}
	}

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getObjectInstance(javax.management.ObjectName)
     */
    public ObjectInstance getObjectInstance(ObjectName arg0)
        throws InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
     */
    public Set queryMBeans(ObjectName arg0, QueryExp arg1)
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#queryNames(javax.management.ObjectName, javax.management.QueryExp)
     */
    public Set queryNames(ObjectName arg0, QueryExp arg1)
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#isRegistered(javax.management.ObjectName)
     */
    public boolean isRegistered(ObjectName arg0)
    {
		return registeredBeans.containsKey ( arg0 );
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getMBeanCount()
     */
    public Integer getMBeanCount()
    {
		return new Integer ( registeredBeans.size() );
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getAttribute(javax.management.ObjectName, java.lang.String)
     */
    public Object getAttribute(ObjectName arg0, String arg1)
        throws
            MBeanException,
            AttributeNotFoundException,
            InstanceNotFoundException,
            ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getAttributes(javax.management.ObjectName, java.lang.String[])
     */
    public AttributeList getAttributes(ObjectName arg0, String[] arg1)
        throws InstanceNotFoundException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#setAttribute(javax.management.ObjectName, javax.management.Attribute)
     */
    public void setAttribute(ObjectName arg0, Attribute arg1)
        throws
            InstanceNotFoundException,
            AttributeNotFoundException,
            InvalidAttributeValueException,
            MBeanException,
            ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#setAttributes(javax.management.ObjectName, javax.management.AttributeList)
     */
    public AttributeList setAttributes(ObjectName arg0, AttributeList arg1)
        throws InstanceNotFoundException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#invoke(javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
     */
    public Object invoke(
        ObjectName arg0,
        String arg1,
        Object[] arg2,
        String[] arg3)
        throws InstanceNotFoundException, MBeanException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getDefaultDomain()
     */
    public String getDefaultDomain()
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    public void addNotificationListener(
        ObjectName arg0,
        NotificationListener arg1,
        NotificationFilter arg2,
        Object arg3)
        throws InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
     */
    public void addNotificationListener(
        ObjectName arg0,
        ObjectName arg1,
        NotificationFilter arg2,
        Object arg3)
        throws InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
     */
    public void removeNotificationListener(
        ObjectName arg0,
        NotificationListener arg1)
        throws InstanceNotFoundException, ListenerNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
     */
    public void removeNotificationListener(ObjectName arg0, ObjectName arg1)
        throws InstanceNotFoundException, ListenerNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");

    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#getMBeanInfo(javax.management.ObjectName)
     */
    public MBeanInfo getMBeanInfo(ObjectName arg0)
        throws InstanceNotFoundException, IntrospectionException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#isInstanceOf(javax.management.ObjectName, java.lang.String)
     */
    public boolean isInstanceOf(ObjectName arg0, String arg1)
        throws InstanceNotFoundException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#deserialize(javax.management.ObjectName, byte[])
     */
    public ObjectInputStream deserialize(ObjectName arg0, byte[] arg1)
        throws InstanceNotFoundException, OperationsException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#deserialize(java.lang.String, byte[])
     */
    public ObjectInputStream deserialize(String arg0, byte[] arg1)
        throws OperationsException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

    /* (non-Javadoc)
     * @see javax.management.MBeanServer#deserialize(java.lang.String, javax.management.ObjectName, byte[])
     */
    public ObjectInputStream deserialize(
        String arg0,
        ObjectName arg1,
        byte[] arg2)
        throws InstanceNotFoundException, OperationsException, ReflectionException
    {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
    }

	public ClassLoader getClassLoader(ObjectName arg0)
			throws InstanceNotFoundException {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

	public ClassLoader getClassLoaderFor(ObjectName arg0)
			throws InstanceNotFoundException {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

	public ClassLoaderRepository getClassLoaderRepository() {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

	public String[] getDomains() {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

	public void removeNotificationListener(ObjectName arg0, ObjectName arg1,
			NotificationFilter arg2, Object arg3)
			throws InstanceNotFoundException, ListenerNotFoundException {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

	public void removeNotificationListener(ObjectName arg0,
			NotificationListener arg1, NotificationFilter arg2, Object arg3)
			throws InstanceNotFoundException, ListenerNotFoundException {
		throw new UnsupportedOperationException ( "TestMBeanServer is a mock object");
	    
	}

}
