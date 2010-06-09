package com.atomikos.beans;
import javax.swing.JOptionPane;

 /**
  *
  *
  *A test class for the beans package.
  */

public class ReleaseTester
{
    private static void test ( Object bean ) throws Exception
    {
        if ( ! ( bean instanceof java.io.Serializable ) )
            System.err.println ( "WARNING: bean is not Serializable!" );
        BeanWizard wizard = new BeanWizard ( bean );
        JOptionPane.showMessageDialog ( null , wizard.getPanel() , "Test" , JOptionPane.PLAIN_MESSAGE );
        System.out.println ( bean.toString() );
        
    }

    public static void main ( String[] args ) throws Exception
    {
        Object bean = null;
        
        if ( args.length == 0 ) bean = new TestBean();
        else {
            Class clazz = Class.forName ( args[0] );
            bean = clazz.newInstance();
        }
        test ( bean );
    }
}
