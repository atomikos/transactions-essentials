package com.atomikos.beans;
import java.awt.Component;
import java.beans.PropertyEditor;

 /**
  *
  *
  *A generic GUI type for editing properties.
  */

public interface PropertyEditorComponent
{
    /**
     *Initializes the component with the editor to delegate to.
     *@param editor The editor.
     */
     
    public void init ( PropertyEditor editor );
    
    /**
     *Get the property editor we delegate to.
     *@return PropertyEditor The editor.
     */
     
    public PropertyEditor getPropertyEditor();
    
    /**
     *Get component for display in GUI.
     *@return Component The component.
     */
     
    public Component getComponent();
}
