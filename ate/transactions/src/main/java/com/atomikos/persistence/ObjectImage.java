package com.atomikos.persistence;

import java.io.Externalizable;

import com.atomikos.util.Identifiable;

/**
 * 
 * 
 * An ObjectImage is a loggable state that can be managed by an ObjectLog.
 * 
 */

public interface ObjectImage extends Externalizable, Identifiable
{
    /**
     * Restore an equivalent replica of the original instance. Called by
     * ObjectLog on recovering the object.
     * 
     * @return Recoverable An equivalent replica of the original.
     * 
     */

    public Recoverable restore ();
}
