//$Id: TSMetaDataImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: TSMetaDataImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:09  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:03:29  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/03/11 01:36:57  guy
//Added MetaData for the UserTransactionService.
//

package com.atomikos.icatch.config.imp;

import com.atomikos.icatch.config.TSMetaData;

/**
 * Copyrigth &copy; 2002, Atomikos. All rights reserved.
 * 
 * A meta data implementation.
 */

public class TSMetaDataImp implements TSMetaData
{
    private String jtaVersion_;

    private String releaseVersion_;

    private boolean supportsImport_;

    private boolean supportsExport_;

    private String productName_;

    /**
     * Create a new instance.
     * 
     * @param jtaVersion
     *            The string denoting the JTA supported.
     * @param releaseVersion
     *            The string denoting the release version.
     * @param productName
     *            The name of the product.
     * @param supportsImport
     *            True if import supported.
     * @param supportsExport
     *            True if export supported.
     */

    public TSMetaDataImp ( String jtaVersion , String releaseVersion ,
            String productName , boolean supportsImport , boolean supportsExport )
    {
        jtaVersion_ = jtaVersion;
        releaseVersion_ = releaseVersion;
        supportsImport_ = supportsImport;
        supportsExport_ = supportsExport;
        productName_ = productName;
    }

    /**
     * @see TSMetaData
     */

    public String getJtaVersion ()
    {
        return jtaVersion_;
    }

    /**
     * @see TSMetaData
     */

    public String getReleaseVersion ()
    {
        return releaseVersion_;
    }

    /**
     * @see TSMetaData
     */

    public boolean supportsImport ()
    {
        return supportsImport_;
    }

    /**
     * @see TSMetaData
     */

    public boolean supportsExport ()
    {
        return supportsExport_;
    }

    /**
     * @see TSMetaData
     */

    public String getProductName ()
    {
        return productName_;
    }
}
