package com.atomikos.vendor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


/**
 * 
 * Copyright &copy; 2008 Atomikos. All rights reserved.
 * 
 *
 * A GUI license creation tool.
 */

public class LicenseTool
implements ActionListener
{
	
	private JFrame frame_;
	
	private JMenuItem evaluationMenu_;
	private JMenuItem hostMenu_;
	private JMenuItem domainMenu_;
	private JMenuItem exitMenu_;
	private JMenuItem developerMenu_;
	private JMenuItem subscriptionMenu_;
	private JMenuItem domainSubscriptionMenu_;
	private JMenuItem oemMenu_;
	
	private ResourceBundle resources_;
	private String[] productNames_;
	
	public LicenseTool ( String[] productNames )
	{
		productNames_ = productNames;
		resources_ = ResourceBundle.getBundle ( "com.atomikos.vendor.LicenseToolResourceBundle");
		frame_ = new JFrame ( resources_.getString ( "mainWindowName"));
		JMenuBar menu = new JMenuBar();
		frame_.setJMenuBar( menu );
		JMenu fileMenu = new JMenu ( resources_.getString( "fileMenuName"));
		menu.add ( fileMenu );
		JMenu newMenu = new JMenu ( resources_.getString ( "newMenuName"));
		fileMenu.add ( newMenu );
		
		evaluationMenu_ = new JMenuItem ( resources_.getString ( "evaluationMenuName"));
		evaluationMenu_.addActionListener ( this );
		newMenu.add( evaluationMenu_ );
		
		hostMenu_ = new JMenuItem ( resources_.getString ( "hostMenuName"));
		hostMenu_.addActionListener ( this );
		newMenu.add ( hostMenu_ );

		domainMenu_ = new JMenuItem ( resources_.getString ( "domainMenuName"));
		domainMenu_.addActionListener ( this );
		newMenu.add ( domainMenu_ );

		subscriptionMenu_ = new JMenuItem ( resources_.getString ( "subscriptionMenuName" ) );
		subscriptionMenu_.addActionListener ( this );
		newMenu.add ( subscriptionMenu_ );
		
		domainSubscriptionMenu_ = new JMenuItem ( resources_.getString ( "domainSubscriptionMenuName" ) );
		domainSubscriptionMenu_.addActionListener ( this );
		newMenu.add ( domainSubscriptionMenu_ );
		
		oemMenu_ = new JMenuItem ( resources_.getString ( "oemMenuName" ) );
		oemMenu_.addActionListener ( this );
		newMenu.add ( oemMenu_ );
		
		exitMenu_ = new JMenuItem ( resources_.getString ( "exitMenuName"));
		exitMenu_.addActionListener ( this );
		fileMenu.add ( exitMenu_ );
		frame_.pack();
		frame_.setSize ( 400 , 300 );
		frame_.setVisible ( true );
		
	}
	
	public void actionPerformed ( ActionEvent event )
	{
		Object source = event.getSource();
		AbstractLicensePanel p = null;
		if ( source == evaluationMenu_ ) {
			p = new EvaluationLicensePanel ( productNames_ , resources_ );
			//LicenseEditor editor = new LicenseEditor ( frame_ , p , resources_ );
		}
		else if ( source == domainMenu_ ) {
			p = new DomainLicensePanel ( productNames_ , resources_ , Integer.MAX_VALUE );
			
		}
		else if ( source == hostMenu_ ) {
			p = new NodeLicensePanel ( productNames_ , resources_ , Integer.MAX_VALUE );
			//LicenseEditor editor = new LicenseEditor ( frame_ , p , resources_ );
		}
		else if ( source == developerMenu_ ) {
			p = new DeveloperLicensePanel ( productNames_ , resources_ );
			//LicenseEditor editor = new LicenseEditor ( frame_ , p , resources_ );
		} else if ( source == subscriptionMenu_ ) {
			p = new NodeLicensePanel ( productNames_ , resources_ , 18 );
		} else if ( source == domainSubscriptionMenu_ ) {
			p = new DomainLicensePanel ( productNames_ , resources_ , 18 );
		} else if ( source == oemMenu_ ) {
			p = new OemLicensePanel ( productNames_ , resources_ );
		}
		else if ( source == exitMenu_ ) {
			System.exit ( 0 );
		}
		EditorThread t = new EditorThread ( frame_ , p , resources_ );
		SwingUtilities.invokeLater(t);
	}
	
    public static void main(String[] args)
    {
    	String[] productNames = { "ExtremeTransactions" };
    	
    	new LicenseTool ( productNames );
    }
}
