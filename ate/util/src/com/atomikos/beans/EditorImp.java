package com.atomikos.beans;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;



 /**
  *
  *
  *A property editor for bean properties.
  *
  */

class EditorImp
implements Editor
{
    private PropertyEditor editor_;
    //the property editor for GUI editing,
    //null if none
    
    private Property property_;
    //the property
    
    private Object editedObject_;
    //the edited object (a local copy since no edit-in-place is done).
    
    private PropertyChangeAdapter adapter_;
    //for converting change events
    
     /** 
      *Creates a new instance for a given editor
      *to delegate to. 
      *@param property The property.
      *@param editor The delegate, or null if none.
      */
      
    EditorImp ( Property property , PropertyEditor editor )
    throws PropertyException
    {
        editor_ = editor; 
        property_ = property;
        try {
            editor.setValue ( property.getValue() );
        }
        catch ( PropertyException writeOnlyProperty ) {
            //ignore; this indicates a write-only property
            //such as Oracle's password property: no getter
        }
        adapter_ = new PropertyChangeAdapter ( this );
        editor_.addPropertyChangeListener ( adapter_ );
    } 
  
    
    protected PropertyEditor getPropertyEditor()
    {
        return editor_; 
    }
    
    public Object getEditedObject()
    {
        return editor_.getValue();
    }
    
    public void setEditedObject ( Object value )
    {
        editor_.setValue ( value ); 
    }
    
    public void addPropertyChangeListener (
        PropertyChangeListener l )
    {
          adapter_.addPropertyChangeListener ( l );
    }
    
    public void removePropertyChangeListener ( 
        PropertyChangeListener l )
    {
        
        adapter_.removePropertyChangeListener ( l ); 
    }
    
    public Property getProperty()
    {
        return property_; 
    }
    
    public void setStringValue ( String val )
    throws PropertyException
    {
//        try {
//          editor_.setAsText ( val );
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//            throw new PropertyException ( e ); 
//        }


		try
        {
        	//do our own wrapper class construction to avoid JDK bugs
            Object o = PrimitiveClasses.createWrapperObject(val,property_.getType());
        	editor_.setValue(o);
        }
        catch (ClassNotPrimitiveException e)
        {
            //e.printStackTrace();
            //happens if a real String class is needed
            //in this case we don't lose anything by trying
            //the JDK required support
            try {
            	editor_.setAsText ( val );
            }
            catch ( Exception err ) {
            	throw new PropertyException( err);
            }
        }
    }

    public String getStringValue()
    throws PropertyException
    {
        return editor_.getAsText();
    }
    
    public Component getComponent()
    {
        return editor_.getCustomEditor();
    }
}
