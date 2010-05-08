//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: ReleaseTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:16  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:45  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:02:45  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:34:04  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.3  2003/05/18 09:42:39  guy
//Added test for serializability of bean.
//
//Revision 1.1.2.2  2003/05/15 15:26:19  guy
//Changed to allow command-line specification of which bean class to use.
//
//Revision 1.1.2.1  2003/05/15 08:04:32  guy
//Added BeanWizard class and debugged bean package to make wizard work.
//

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