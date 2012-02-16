/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

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
