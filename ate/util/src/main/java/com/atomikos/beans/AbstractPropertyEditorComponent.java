package com.atomikos.beans;
import java.awt.Component;
import java.beans.PropertyEditor;

 /**
  *
  *
  *An abstract implementation of PropertyEditorComponent.
  */

public abstract class AbstractPropertyEditorComponent
implements PropertyEditorComponent
{
    private PropertyEditor editor_;
    //the editor to set/get values from
    
    
    public AbstractPropertyEditorComponent()
    {
        editor_ = null;
    }
  
   /**
    *@see PropertyEditorComponent
    */
    
   public PropertyEditor getPropertyEditor()
   {
        return editor_;
   }
    
    /**
    *@see PropertyEditorComponent
    */
    
   public void init ( PropertyEditor editor )
   {
        editor_ = editor;
   }
   
   /**
    *To be overridden in subclasses.
    */
    
   public abstract Component getComponent();
}
