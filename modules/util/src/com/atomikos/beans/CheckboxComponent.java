package com.atomikos.beans;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

 /**
  *
  *
  *An editor component that allows editing of boolean values.
  */

public class CheckboxComponent
extends AbstractPropertyEditorComponent
implements ActionListener
{
    private JCheckBox checkbox_;
    //the checkbox
    
    private JPanel panel_;
    //the component to return to clients
    
    public CheckboxComponent ( Property property )
    throws PropertyException
    {
        super();
        panel_ = new JPanel();
        Boolean selected = ( Boolean ) property.getValue();
        checkbox_ = new JCheckBox();
        checkbox_.setSelected (  selected.booleanValue() );
        checkbox_.setToolTipText ( property.getDescription() );
        panel_.add ( checkbox_ );
        checkbox_.addActionListener ( this );
    }
    
    /**
    *@see PropertyEditorComponent
    */
    
   public Component getComponent()
   {
        return panel_;
   }
    
  /**
   *@see ActionListener
   */
   
   public void actionPerformed ( ActionEvent e )
   {
        try {
            getPropertyEditor().setValue ( 
                new Boolean ( checkbox_.isSelected() ) );
        }
        catch ( Exception err ) {
            err.printStackTrace(); 
        }
   }
}
