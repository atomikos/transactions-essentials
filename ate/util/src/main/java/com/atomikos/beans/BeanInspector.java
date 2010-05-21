package com.atomikos.beans;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;


 /**
  *
  *
  *A bean inspector is capable of detecting what properties can
  *be set in a bean, setting or getting any such values 
  *and returning useful information about the bean.
  */

public class BeanInspector
{
    private Object bean_;
    //the bean instance for inspection/creation
    
   
     /** 
      *Creates a new instance to configure a given bean.
      *@param bean The bean to inspect and configure.
      */
      
    public BeanInspector ( Object bean )
    {
        bean_ = bean; 
    }
    

    
     /**
      *Get all recognized properties for this bean.
      *@return Property[] The properties found,
      *or null  if none.
      */
      
    public Property[] getProperties()
    throws PropertyException
    {
        Property[] ret = null;
        try {
            Class clazz = bean_.getClass();
            BeanInfo info = Introspector.getBeanInfo ( clazz );
            if ( info != null ) {
                PropertyDescriptor[] props = info.getPropertyDescriptors();
                if ( props != null ) {
                    ret = new Property[props.length];
                    for ( int i = 0 ; i < props.length ; i++ ) {
                        if ( props[i] instanceof IndexedPropertyDescriptor ) {
                            IndexedPropertyDescriptor d = 
                            ( IndexedPropertyDescriptor ) props[i];
                                ret[i] = new IndexedPropertyImp ( bean_ , d );

                        }
                        else {

                                ret[i] = new PropertyImp ( bean_ , props[i] );
                                //System.out.println ( "Found property : " + ret[i].getName() );

                        }
                    }
                }
            }
        }
        catch ( Exception e ) {
            //e.printStackTrace();
            throw new PropertyException ( "Error getting properties" , e );
        }
        
        return ret;
    }
    
     /**
      *Get the property with the given name.
      *@param name The name of the property.
      *@return Property The property, null if not found.
      */
      
    public Property getProperty ( String name )
   	throws PropertyException
    {
          Property ret = null;
          Property[] properties = getProperties();
          if ( properties != null ) {
          	for ( int i = 0 ; i < properties.length ; i ++ ) {
          		if ( properties[i].getName().equals ( name )) 
          			ret = properties[i];
          	}
          }
          return ret;
    }
    
    
    /**
     *Get the bean instance.
     *
     *@return Object The bean instance.
     */
     
    public Object getBean()
    {
        return bean_;
    }
	
	/**
	 * Set the given property to the given text value.
	 * 
	 * @param name The name of the property
	 * @param value The value as text
	 * @throws ReadOnlyException If readonly
	 * @throws PropertyException On failure
	 */
	public void setPropertyValue ( String name , String value ) 
	throws ReadOnlyException, PropertyException
	{
			
				
			Property p = getProperty ( name );
			if ( p == null ) 
			throw new PropertyException ( "No such property: " + name );
			p.getEditor().setStringValue ( value );
			p.setValue(p.getEditor().getEditedObject());
	}
	
	/**
	 * Get the value of the given property as text.
	 * @param name The name of the property.
	 * @return String The value as text.
	 * @throws PropertyException
	 */
	
	public String getPropertyValue ( String name )
	throws PropertyException
	{    
		String ret = "";
		Property p = getProperty ( name );
		Object val = p.getValue();
		if ( val != null ) ret = val.toString();
		return ret;
	}
    
}
