package com.atomikos.beans;
import java.beans.IndexedPropertyDescriptor;
import java.lang.reflect.Method;
 
  /**
   *
   *
   *An implementation of indexed property.
   */
  
class IndexedPropertyImp
extends PropertyImp
implements IndexedProperty
{
    private IndexedPropertyDescriptor descriptor_;
    
    /** 
      *Creates a new instance for a given editor
      *to delegate to. 
      *@param property The property.
      *@param editor The delegate, or null if none.
      */
      
    IndexedPropertyImp ( Object bean , IndexedPropertyDescriptor descriptor )
    throws PropertyException
    {
        super ( bean , descriptor );
        descriptor_ = descriptor;
    } 
    
     /**
      *@see IndexedProperty
      */
      
    public Object getValue ( int i )
    throws PropertyException
    {
         Object ret = null;
         
         try {
             Method m = descriptor_.getIndexedReadMethod();
             Integer index = new Integer ( i );
             Object[] args = new Object[1];
             args[0] = index;
             ret = m.invoke ( getBean() , args );
         }
         catch ( Exception e ) {
              throw new PropertyException ( "Error getting property" , e );
         }
         
         return ret;
    }
    
     /**
      *@see IndexedProperty
      */
      
    public boolean isIndexReadOnly()
    {
        return descriptor_.getIndexedWriteMethod() == null;
    }
    
    /**
     *@see IndexedProperty
     */
     
    public void setValue ( int i , Object arg )
    throws ReadOnlyException, PropertyException
    {
        if ( isIndexReadOnly() ) {
              throw new ReadOnlyException ( "Property is readonly" ); 
          }
          
          try {
              Object[] args = new Object[2];
              args[0] = new Integer ( i );
              args[1] = arg;
              Method method = descriptor_.getIndexedWriteMethod();
              method.invoke ( getBean() , args );
          }
          catch ( Exception e ) {
              throw new PropertyException ( "Error in setting value" , e ); 
          } 
    }
    
     /**
      *@see IndexedProperty
      */
      
    public Class getIndexedType()
    {
        return descriptor_.getIndexedPropertyType(); 
    }

    /**
     *@see Property 
     */

    public IndexedProperty getIndexedProperty()
    {
        return this;

    }

    //     /**
//      *@see IndexedProperty
//      */
//      
//    public IndexedEditor getIndexedEditor()
//    {
//          PropertyEditor pedit = 
//              PropertyEditorManager.findEditor ( descriptor_.getPropertyType() );
//          return new IndexedEditorImp ( this , pedit );
//    }
}
