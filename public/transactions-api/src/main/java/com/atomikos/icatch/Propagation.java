/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.util.Properties;
import java.util.Stack;

/**
 * Information about the transaction context that can be 
 * shipped along with a remote request (via toString()), to make the other side
 * participate in the transaction present for the current thread in this VM.
 * 
 * This class (and its parsing) represent the minimum information required for
 * transactions to work across remoting calls. All values are required.
 * 
 * In addition, we are liberal in parsing: any additional, unknown values are ignored - 
 * so future releases can add extra, optional properties and still work with 
 * installations of this release.
 * 
 */

public class Propagation {
    
    /**
     * Major version indicator. Only change this for future releases 
     * that add incompatible changes such as adding/removing required properties
     * and/or changing the semantics of existing properties. As long as optional properties
     * are added/removed, the version can stay the same.
     */
    public static final String VERSION ="2019";
    
    private final Stack<CompositeTransaction> lineage;
    private final boolean serial;
    private final long timeout;
    private final String recoveryDomainName;
    private final String recoveryCoordinatorUri;

    public Propagation (String recoveryDomainName,CompositeTransaction rootTransaction, CompositeTransaction parentTransaction, boolean serial, long timeout) {
        this(recoveryDomainName, rootTransaction, parentTransaction, serial, timeout, null);
    }
    
    public Propagation (String recoveryDomainName,CompositeTransaction rootTransaction, CompositeTransaction parentTransaction, boolean serial, long timeout, String recoveryCoordinatorUri) {
        if (rootTransaction == null) {
            throw new IllegalArgumentException("rootTransaction cannot be null");
        }
        if (parentTransaction == null) {
            throw new IllegalArgumentException("parentTransaction cannot be null");
        }
        if (recoveryCoordinatorUri == null) {
            //default to parent coordinator ID
            recoveryCoordinatorUri = parentTransaction.getCompositeCoordinator().getCoordinatorId();
        }
        this.timeout = timeout;
        this.serial = serial;
        this.recoveryDomainName = recoveryDomainName;
        this.lineage = new Stack<CompositeTransaction>();
        this.lineage.push(rootTransaction);
        this.lineage.push(parentTransaction);
        this.recoveryCoordinatorUri = recoveryCoordinatorUri;
    }
    
    public String getRecoveryDomainName() {
        return recoveryDomainName;
    }
    
    public CompositeTransaction getRootTransaction() {      
        return this.getLineage().firstElement();
    }
    
    
    public CompositeTransaction getParentTransaction() {
        return this.lineage.lastElement();
    }

    /**
     * @return A stack of ancestors, bottom one is the root.
     */
    
    public Stack<CompositeTransaction> getLineage() {
        return lineage;
    }

    
    public boolean isSerial() {
        return serial;
    }

    public long getTimeout() {
        return timeout;
    }
    
    public String getRecoveryCoordinatorURI() {
        return recoveryCoordinatorUri;
    }
    
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("version=").append(VERSION).append(",");
        ret.append("domain=").append(recoveryDomainName).append(",");
        ret.append("timeout=").append(getTimeout()).append(",");
        ret.append("serial=").append(isSerial()).append(",");
        ret.append("recoveryCoordinatorURI=").append(recoveryCoordinatorUri).append(",");
        addParent(getRootTransaction(), ret);
        if (!getRootTransaction().isSameTransaction(getParentTransaction())) {
            ret.append(",");
            addParent(getParentTransaction(), ret);            
        }
        return ret.toString();
    }
    
    private void addParent(CompositeTransaction parent, StringBuffer buf) {
        buf.append("parent=").append(parent.getTid());
        Properties p = parent.getProperties();
        for (String key : p.stringPropertyNames()) {
            buf.append(",").
            append("property.").append(key). //use prefix to avoid name collisions between property keys and propagation attributes
            append("=").append(p.getProperty(key));
        }
    }
}
