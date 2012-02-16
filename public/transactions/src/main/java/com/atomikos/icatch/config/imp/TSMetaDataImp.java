/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

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
