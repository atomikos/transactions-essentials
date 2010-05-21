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
