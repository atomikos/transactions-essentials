package com.atomikos.vendor;
import java.util.ResourceBundle;

import javax.swing.JFrame;
/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A thread for showing the editor windows.
 */

public class EditorThread implements Runnable
{
	private AbstractLicensePanel panel_;
	private ResourceBundle resources_;
	private String[] productNames_;
	private JFrame parent_;
	
	public EditorThread ( JFrame parent , AbstractLicensePanel panel , 
		 ResourceBundle resources)
	{
		panel_ = panel;
		resources_ = resources;
		parent_= parent;
	}
    
    public void run()
    {
        LicenseEditor editor = new LicenseEditor ( parent_ , panel_ , resources_ );

    }

}
