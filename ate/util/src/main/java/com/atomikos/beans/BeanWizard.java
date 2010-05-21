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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

 /**
  *
  *
  *A GUI setup tool for configuring arbitrary bean instances.
  *This tool can be used by other GUI programs to setup
  *the properties of an arbitrary bean. Afterwards, object
  *serialization can be used to persist the bean and its
  *settings. This tool can be combined with a Dialog: if the
  *dialog is confirmed then getBean will return the
  *bean as it was configured.
  */

public class BeanWizard
implements PropertyChangeListener
{
    /**
     *Filter the given properties to contain only those to be managed
     *by a GUI editor.
     *@param props The original properties.
     *@return Property[] An array of properties for GUI editing.
     */

    private static Property[] filterProperties ( Property[] props )
    throws PropertyException
    {
        if ( props == null ) return null;
        
        ArrayList list = new ArrayList();
        
        for ( int i = 0 ; i < props.length ; i++ ) {
            if ( ! (  props[i].isHidden() ||
                      props[i].isReadOnly() ||
                      props[i].getEditor() == null ) )
                list.add ( props[i] );
//            else {
//                if ( props[i].getEditor() == null ) System.out.println ( "Beanwizard: filter out property " + props[i].getName() );
//                if ( props[i].isReadOnly()  ) System.out.println ( "Beanwizard: filter out readonly property " + props[i].getName() );
//                if ( props[i].isHidden()  ) System.out.println ( "Beanwizard: filter out hidden property " + props[i].getName() );
//
//            }
        }
        return ( Property[] ) list.toArray ( new Property[0] );
    }
    
    private JPanel panel_;
    //the panel to display in a client program

    private BeanInspector inspector_;
    //the bean inspector

    public BeanWizard ( Object bean ) throws PropertyException
    {
        panel_ = new JPanel();
        panel_.setLayout ( new BorderLayout() );
        JPanel tempPanel = new JPanel();
        inspector_ = new BeanInspector ( bean );
        Property[] properties = filterProperties ( inspector_.getProperties() );
        int rows = 0;
        if ( properties != null ) rows = properties.length;
        tempPanel.setLayout ( new GridLayout ( rows , 1 ) );
        for ( int i = 0 ; i < rows ; i++ ) {
            JPanel propertyPanel = new JPanel();
           
            propertyPanel.setLayout ( new GridLayout ( 1 , 2 ) );
            JLabel nameLabel = new JLabel ( properties[i].getName() );
            //set the name to bold if the property is essential
            if ( properties[i].isPreferred() ) {
                Font font = new Font ( "SansSerif" , Font.BOLD , 12 );
                nameLabel.setFont ( font );
            }
            else {
                Font font = new Font ( "SansSerif" , Font.PLAIN , 12 );
                nameLabel.setFont ( font );
            }
            propertyPanel.add ( nameLabel );
            //use the property's custom GUI component for editing
            if ( properties[i].getIndexedProperty() != null ) {
                JButton button = new JButton ( "Edit" );
                button.addActionListener ( new ButtonListener ( properties[i] ) );
                propertyPanel.add ( button );
            }
            else
                propertyPanel.add ( properties[i].getEditor().getComponent() );
            properties[i].getEditor().addPropertyChangeListener ( this );
            
            tempPanel.add ( propertyPanel );
            panel_.setPreferredSize ( new Dimension ( 300 , 300 ) );
            panel_.add ( new JScrollPane ( tempPanel ) , BorderLayout.CENTER );
            
        }
        
    }

    /**
     *Get the panel to display in the client program.
     *This panel is the main panel for the wizard
     *and allows setting the properties of the bean.
     *
     *@return JPanel The panel.
     */
    
    public JPanel getPanel()
    {
        return panel_;
    }

    /**
     *Get the bean.
     *This method can be used to retrieve the bean after the
     *user is done with the wizard.
     *
     *@return Object The bean instance.
     */
    
    public Object getBean()
    {
        if ( inspector_ == null ) return null;
        else return inspector_.getBean();
    }

    public void propertyChange ( PropertyChangeEvent event )
    {
        Editor editor = ( Editor ) event.getSource();
        Property property = editor.getProperty();
        System.err.println ( "BeanWizard: propertyChange" );
        try {
            System.err.println ( editor.getEditedObject().getClass().getName() );
            property.setValue ( editor.getEditedObject() );
            System.err.println ( "Property set: " + property.getName() );
            System.err.println ( "to value: " + editor.getEditedObject() );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    static class ButtonListener implements ActionListener
    {
        private Property property_;
        //an indexed property

        ButtonListener ( Property property )
        {
            property_ = property;
        }

        public void actionPerformed ( ActionEvent e )
        {
            try {
                JOptionPane.showMessageDialog ( null , property_.getEditor().getComponent() ,
                                                "Edit" , JOptionPane.PLAIN_MESSAGE  );
            }
            catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }
}
