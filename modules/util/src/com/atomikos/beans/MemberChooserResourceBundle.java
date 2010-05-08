package com.atomikos.beans;

import java.util.ListResourceBundle;

/**
 *
 *
 *
 *
 */
public class MemberChooserResourceBundle
        extends ListResourceBundle
{
    protected Object[][] getContents ()
    {
        return new Object[][]{
            {"methodsDialogTitle" , "Select Method"},
            {"fieldsDialogTitle" , "Select Field"},
            {"constructorsDialogTitle" , "Select Constructor"},
            {"methodsDialogMessage" , "Please select method"},
            {"fieldsDialogMessage" , "Please select field"},
            {"constructorsDialogMessage" , "Please select constructor"}
        };
    }
}
