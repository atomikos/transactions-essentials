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
