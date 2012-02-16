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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
  *
  *
  *An editor component that displays a property in a text field.
  */

public class TextFieldComponent
extends AbstractPropertyEditorComponent
implements CaretListener //ActionListener
{

    private JTextField text_;
    //the text field

    private JPanel panel_;
    //the component that can be returned to clients

    private Property property_;

    private boolean decimal_;
    //true iff numeric and decimal point allowed

    public TextFieldComponent ( Property property )
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
         text_ = new JTextField ( 10 );
         layout.setConstraints ( text_ , c );
         text_.setToolTipText ( property.getDescription() );
         panel_.add ( text_ );
         text_.addCaretListener ( this );
    }

    public TextFieldComponent ( Property property , boolean allowsDecimalPoint )
        throws PropertyException
    {
        this ( property );
        decimal_ = allowsDecimalPoint;
        text_.addKeyListener ( new KeyAdapter() {
            public void keyTyped ( KeyEvent e ) {
                char c = e.getKeyChar();
                if ( ! ( ( Character.isDigit ( c ) ) ||
                         ( c == KeyEvent.VK_BACK_SPACE ) ||
                         ( c == KeyEvent.VK_DELETE ) ||
                         ( c == KeyEvent.VK_PERIOD && decimal_ ) ) ) {
                    e.consume();
                    panel_.getToolkit().beep();
                }
            }} );
    }

   /**
    *@see PropertyEditorComponent
    */

   public Component getComponent()
   {
        try {
            //text_.setPreferredSize ( new Dimension ( 100 , 100 ) );
            text_.setText ( property_.getEditor().getStringValue() );
            //text_.setColumns ( text_.getText().length() + 3 );
        }
        catch ( PropertyException e ) { }
        return panel_;
   }

   public void actionPerformed ( ActionEvent e )
   {
        Class wrapperClass =
          PrimitiveClasses.getWrapperClass ( property_.getType() );
        try {
            if ( wrapperClass == null ) {
                getPropertyEditor().setAsText ( text_.getText() );

            }
            else {
                Object value = PrimitiveClasses.createWrapperObject (
                    text_.getText() , property_.getType() );
                getPropertyEditor().setValue ( value );
            }
            //text_.setColumns ( text_.getText().length() +  3 );

        }
        catch ( Exception err ) {
              err.printStackTrace();
        }
   }

    public void caretUpdate ( CaretEvent e )
    {
        if ( text_.getText() == null || text_.getText().equals ( "" ) ) return;

        Class wrapperClass =
        PrimitiveClasses.getWrapperClass ( property_.getType() );
        try {
            if ( wrapperClass == null ) {
                getPropertyEditor().setAsText ( text_.getText() );
            }
            else {
                Object value = PrimitiveClasses.createWrapperObject (
                                                                     text_.getText() , property_.getType() );
                getPropertyEditor().setValue ( value );
            }
            //text_.setColumns ( text_.getText().length() +  3 );

        }
        catch ( Exception err ) {
            err.printStackTrace();
        }
    }

}
