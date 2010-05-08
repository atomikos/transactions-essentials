//$Id: ComboBoxComponent.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: ComboBoxComponent.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:14  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:50  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:40  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:42  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:23:04  guy
//Updated javadoc.
//
//Revision 1.2  2004/03/22 15:34:01  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2003/05/18 09:42:22  guy
//Added dropdown list support for fixed value set properties.
//

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