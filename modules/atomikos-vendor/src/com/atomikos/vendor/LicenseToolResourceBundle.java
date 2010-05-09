package com.atomikos.vendor;

import java.util.ListResourceBundle;


/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * 
 */

public class LicenseToolResourceBundle extends ListResourceBundle
{

   
    protected Object[][] getContents()
    {
        Object[][] ret = {
        	{ "mainWindowName" , "Atomikos License Tool"},
        	{ "newMenuName" , "New"},
        	{ "exitMenuName" , "Exit"},
        	{ "fileMenuName" , "File"},
        	{ "evaluationMenuName" , "Evaluation (1M)"},
        	{ "subscriptionMenuName" , "Node Subscription (18M)" },
        	{ "domainSubscriptionMenuName" , "Site Subscription (18M)" },
        	{ "developerMenuName" , "Developer License"},
        	{ "hostMenuName" , "Node (No Expiry)"},
        	{ "domainMenuName" ,"Site (No Expiry)"},
        	{ "oemMenuName" ,"OEM (No Expiry)"},
        	{ "productNameLabel" , "Product Name: "},
        	{ "maxVersionLabel" , "Maximum Version Allowed: "},
        	{ "secretKeyLabel" , "Secret License Key: "},
        	{ "ownerLabel" , "License Owner (Name or Email): "},
        	{ "editorTitle" , "License Editor"},
        	{ "saveButtonName" , "Save"},
        	{ "cancelButtonName" , "Cancel"},
        	{ "domainLabel" , "Domain Name: "},
        	{ "hostsColumnName", "IP Addresses of Allowed Hosts"},
        	{ "editHostTitle" , "Enter IP Address"},
        	{ "saveTitle" , "Select Destination"},
			{ "featureNameLabel" , "Feature Name"},
			{ "featureValueLabel" , "Feature Value"}
        	
        };
        
        return ret;
    }

    

}
