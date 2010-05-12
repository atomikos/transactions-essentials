package com.atomikos.vendor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.atomikos.license.License;


/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A GUI editor for evaluation license generation.
 */

class LicenseEditor implements ActionListener
{
	private ResourceBundle resources_;
	private JFrame parent_;
	private JFrame frame_;
	private AbstractLicensePanel panel_;
	
	private JButton saveButton_ , cancelButton_;
	
	
	
	LicenseEditor ( JFrame parent , AbstractLicensePanel panel , ResourceBundle resources )
	{
		parent_ = parent;
		panel_ = panel;
		resources_ = resources;
		
		frame_ = new JFrame ( getResource ( "editorTitle" ));
		JPanel buttonPanel = new JPanel();
		
		saveButton_ = new JButton ( getResource ("saveButtonName"));
		saveButton_.addActionListener ( this );
		buttonPanel.add ( saveButton_ );
		cancelButton_ = new JButton ( getResource ( "cancelButtonName"));
		cancelButton_.addActionListener ( this );
		buttonPanel.add ( cancelButton_ );
		frame_.getContentPane().add( buttonPanel , BorderLayout.SOUTH );
		frame_.getContentPane().add ( panel_.getJPanel() );
		frame_.pack();
		frame_.setVisible ( true );
		frame_.setResizable(false);
	}
	
	private String getResource ( String name )
	{
		return resources_.getString ( name );
	}
	
	public void actionPerformed ( ActionEvent event )
	{
		if ( event.getSource() == cancelButton_ ) {
			frame_.dispose();
		}
		else {
			//SAVE PRESSED
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle ( getResource ( "saveTitle"));
			fc.setFileSelectionMode ( JFileChooser.DIRECTORIES_ONLY );
			if ( fc.showOpenDialog( frame_ ) == JFileChooser.APPROVE_OPTION ) {
				
				try {
					java.io.File destinationDir = fc.getSelectedFile();
					File tempDir = new File ( destinationDir , "tmp");
					if ( !tempDir.exists() ) tempDir.mkdir();
					String baseName = tempDir.getAbsolutePath() +
					java.io.File.separator;
					panel_.saveToFile ( baseName );
					//generate jar file
					JarArchive jar = new JarArchive (tempDir);
					jar.createJar ( License.LICENSE_FILE_BASE_NAME + ".jar" , destinationDir );
					
				}
				catch ( java.io.IOException io ) {
					io.printStackTrace();
				}
				frame_.dispose();
			}
		}
	}

}
