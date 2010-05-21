package com.atomikos.persistence;

import com.atomikos.util.Identifiable;

/**
 * 
 * 
 * Recoverable interface: supports images for object reconstruction.
 * 
 */

public interface Recoverable extends Identifiable
{

    /**
     * Get an object image for this instance. Allows later reconstruction of the
     * instance.
     */

    public ObjectImage getObjectImage ();

}
