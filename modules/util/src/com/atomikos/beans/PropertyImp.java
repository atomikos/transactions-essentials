package com.atomikos.beans;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

 /**
  *
  *
  *A default implementation of a Property.
  */
  
class PropertyImp
implements Property
{
      private PropertyDescriptor descriptor_;
      
      private Object bean_;

      private Editor editor_;
      
      /**
        *Creates a new instance that delegates to 
        *an underlying KeyValuePair.
        *@param bean The bean.
        *@param descriptor The property descriptor.
        */
        
      PropertyImp ( Object bean , PropertyDescriptor descriptor )
      throws PropertyException
      {
          descriptor_ = descriptor; 
          bean_ = bean;
          try {
            editor_ = createEditor();
          }
          catch ( PropertyException ignore ) {
              //ignore.printStackTrace();
            //workaround for Oracle URL property: throws SQLException!
          }
      }

      
      protected PropertyDescriptor getPropertyDescriptor()
      {
          return descriptor_; 
      }
      
      protected Object getBean()
      {
          return bean_; 
      }
      
       /**
        *@see Property 
        */
        
      public String getName()
      {
          return descriptor_.getName();
      }
      
      /**
        *@see Property 
        */
        
      public String getDescription()
      {
          String ret = null;
          ret = descriptor_.getShortDescription();
          if ( ret == null ) ret = getName();
          return ret;
      }
      
      /**
        *@see Property 
        */
      
      public Class getType()
      {
          return descriptor_.getPropertyType(); 
      }
      
      /**
        *@see Property 
        */
      
      public boolean isExpert()
      {
          return descriptor_.isExpert(); 
      }
      
      /**
        *@see Property 
        */
      
      public boolean isPreferred()
      {
          return descriptor_.isPreferred(); 
      }
      
      /**
        *@see Property 
        */
      
      public boolean isHidden()
      {
          return descriptor_.isHidden(); 
      }
      
      /**
        *@see Property 
        */
      
      public boolean isReadOnly()
      {
          return descriptor_.getWriteMethod() == null;
      }
      
      /**
        *@see Property 
        */
      
      public Object getValue()
      throws PropertyException
      {
          Object ret = null;
          try {
              Method method = descriptor_.getReadMethod();
              ret = method.invoke ( bean_ , null );
              
          }
          catch ( InvocationTargetException e ) {
              //e.getTargetException().printStackTrace();
              throw new PropertyException ( "Error getting value of " + descriptor_.getName() +" " + e.getMessage() , e.getTargetException()  );
          }
          catch ( Exception err ) {
              throw new PropertyException ( "Error getting value of " + descriptor_.getName() , err );
          }
          return ret;
      }
      
      /**
        *@see Property 
        */
      
      public IndexedProperty getIndexedProperty()
      {
          return null;
          
      }
      
      /**
        *@see Property 
        */
      
      public void setValue ( Object arg )
      throws ReadOnlyException, PropertyException
      {
          if ( isReadOnly() ) {
              throw new ReadOnlyException ( "Property is readonly" ); 
          }
          
          try {
          	  
              Object[] args = new Object[1];
              args[0] = arg;
              Method method = descriptor_.getWriteMethod();
              method.invoke ( bean_ , args );
          }
          catch ( Exception e ) {
              e.printStackTrace();
              throw new PropertyException ( "Error in setting value" , e ); 
          }
          
      }
      
      /**
        *@see Property 
        */
      
      private Editor createEditor()
      throws PropertyException
      {
          Editor ret = null;
          PropertyEditorComponent component = null;
          Class wrapperClass = PrimitiveClasses.getWrapperClass ( getType() );
          
          if ( wrapperClass != null ) {
              if ( wrapperClass.equals ( Boolean.class ) ) {
                  component = new CheckboxComponent ( this );
              }
              else {
                  component = new TextFieldComponent ( this ,
                                                       PrimitiveClasses.isDecimalClass ( wrapperClass ) );
              }
          }
          else if ( String.class.equals ( getType() ) ) {
              if ( getAllowedValues() != null ) component = new ComboBoxComponent ( this );
              else component = new TextFieldComponent ( this ); 
          }
          else  if ( getIndexedProperty() != null ) {
              //not a primitive type but an array
              component = new TableComponent ( getIndexedProperty() );
              
          }
          
          if ( component != null ) {
              PropertyEditor pedit = 
                  new DefaultPropertyEditor ( component , getAllowedValues() );
                  
              ret = new EditorImp ( this , pedit );
          }
          
          return ret;
      }

      /**
       *@see Property
       */
      
      public Editor getEditor()
      {
          return editor_;
      }

      /**
        *@see Property 
        */
      
      public String[] getAllowedValues()
      {
          String[] ret = null;
          PropertyEditor pedit = null;

          try {
              //try to see if a custom property editor was set
              Class clazz = descriptor_.getPropertyEditorClass();
              if ( clazz != null ) pedit = ( PropertyEditor ) clazz.newInstance();
          }
          catch ( Exception e ) {
              //e.printStackTrace();
              //ignore: just don't use the custom editor
          }
                  
          if ( pedit == null ) {
              pedit = PropertyEditorManager.findEditor ( descriptor_.getPropertyType() );
          }
                        
          if ( pedit != null ) {
              ret = pedit.getTags();
          }
          return ret;
      }
}
