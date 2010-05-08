package com.atomikos.swing;

import java.util.ListResourceBundle;

/**
 *
 *
 *
 *A resource bundle for the locale-specific values in the properties panel.
 */
public class PropertiesPanelResourceBundle
        extends ListResourceBundle
{
    private static final Object[][] contents_ =
            {
                {"viewButtonName" , "View" },
                {"deleteButtonName" , "Delete" },
                {"newButtonName" , "New" },
                {"editButtonName" , "Edit" }
            };
    protected Object[][] getContents ()
    {
        return contents_;
    }

}
