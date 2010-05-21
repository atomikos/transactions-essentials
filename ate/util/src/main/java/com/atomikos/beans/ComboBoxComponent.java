package com.atomikos.beans;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

 /**
  *
  *
  *A property editor component for a combo list of values.
  */

public class ComboBoxComponent
extends AbstractPropertyEditorComponent
implements ActionListener
{
    private JComboBox comboBox_;
    //the dropdown list

    private JPanel panel_;
    //what is returned to clients

    private Property property_;

    public ComboBoxComponent ( Property property )
    throws PropertyException
    {
        super();
        property_ = property;
        panel_ = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel_.setLayout ( layout );
        GridBagConstraints c = new GridBagConstraints();
        c.fill =  GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel ( property.getName() );
        comboBox_ = new JComboBox ( property_.getAllowedValues() );
        layout.setConstraints ( comboBox_ , c );
        comboBox_.setToolTipText ( property.getDescription() );
        panel_.add ( comboBox_ );
        comboBox_.addActionListener ( this );
    }


    /**
     *@see PropertyEditorComponent
     */

    public Component getComponent()
    {

        return panel_;
    }
   
    public void actionPerformed ( ActionEvent e )
    {
        Class wrapperClass = 
        PrimitiveClasses.getWrapperClass ( property_.getType() );
        try {
            if ( wrapperClass == null ) {
                getPropertyEditor().setAsText ( ( String ) comboBox_.getSelectedItem() );

            }
           
        }
        catch ( Exception err ) {
            err.printStackTrace();
        }
    }
}
