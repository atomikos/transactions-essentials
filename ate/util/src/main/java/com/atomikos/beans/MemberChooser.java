package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 *
 *
 *This class offers a GUI dialog for choosing methods, constructors or fields
 *from a given class. Its behaviour can be customized by providing a
 *MemberFilter that restricts the possible options.
 */

public class MemberChooser
{
    Class clazz_;
    //the class to inspect

    ClassInspector inspector_;

    JFrame parent_;

    ResourceBundle messages_ ;


    public MemberChooser ( JFrame parent , Class clazz )
    {
        clazz_ = clazz;
        parent_ = parent;
        inspector_ = new ClassInspector ( clazz );
        messages_ = ResourceBundle.getBundle ( "com.atomikos.beans.MemberChooserResourceBundle");
    }

    public void setMemberFilter ( MemberFilter filter )
    {
        inspector_.setMemberFilter ( filter );
    }

    public MemberFilter getMemberFilter()
    {
        return inspector_.getMemberFilter();
    }

    /**
     * Shows a dialog with all the applicable methods.
     * @param selectedMethod The method that is initially selected.
     * @return  Method The selected method, or null if cancelled.
     */

    public Method showMethodsDialog ( Method selectedMethod )
    {
       Method[] methods = inspector_.getMethods();
       Method ret =
               ( Method ) JOptionPane.
               showInputDialog ( parent_ ,
                                 messages_.getString ( "methodsDialogTitle") ,
                                 messages_.getString ( "methodsDialogMessage"),
                                 JOptionPane.QUESTION_MESSAGE , null ,
                                 methods , selectedMethod );
       return ret;
    }

    /**
     * Shows a dialog with all applicable fields.
     *
     * @param selectedField The initially selected field.
     * @return Field The selected field, or null if cancelled.
     */

    public Field showFieldsDialog ( Method selectedField )
    {
       Field[] fields = inspector_.getFields();
       Field ret =
               ( Field ) JOptionPane.
               showInputDialog ( parent_ ,
                                 messages_.getString ( "fieldsDialogTitle") ,
                                 messages_.getString ( "fieldsDialogMessage"),
                                 JOptionPane.QUESTION_MESSAGE , null ,
                                 fields , selectedField );
       return ret;
    }

    /**
     * Shows a dialog with all applicable constructors.
     *
     * @param selectedConstructor  The initially selected constructor.
     * @return Constructor The constructor, or null if cancelled.
     */

    public Constructor showConstructorsDialog ( Constructor selectedConstructor )
    {
       Constructor[] methods = inspector_.getConstructors();
       Constructor ret =
               ( Constructor ) JOptionPane.
               showInputDialog ( parent_ ,
                                 messages_.getString ( "constructorsDialogTitle") ,
                                 messages_.getString ( "constructorsDialogMessage"),
                                 JOptionPane.QUESTION_MESSAGE , null ,
                                 methods , selectedConstructor );
       return ret;
    }

    public static void main ( String[] args ) throws Exception
    {
        if ( args.length == 0 ) throw new Exception ( "Missing arg: class name");
        String className = args[0];
        Class clazz = Class.forName ( className );
        MemberChooser chooser = new MemberChooser ( null , clazz );
        Method m = chooser.showMethodsDialog ( null );
        if ( m!= null ) System.out.println ( m.toString() );
        Field f = chooser.showFieldsDialog ( null );
        if ( f != null ) System.out.println ( f.toString() );
        Constructor c = chooser.showConstructorsDialog ( null );
        if ( c != null ) System.out.println ( c.toString() );
    }

}
