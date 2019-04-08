/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Collection;

import com.atomikos.datasource.RecoverableResource;

 /**
  * Lookup strategy for finding the resources to recover.
  */

@FunctionalInterface
public interface ResourceLookup {

    Collection<RecoverableResource> getResources();
    
}
