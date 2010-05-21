package com.atomikos.util;

/**
 *
 *
 *Identifiable objects are those that have a unique object ID,
 *which can be application-dependent.
 *Ideally, this ID can be used to reference the object, even after it has been
 *moved to stable storage.
 */

public interface Identifiable
{
    /**
     *Get the id.
     *
     *@return Object The id, should be the same as returned by the
     *corresponding logimage.
     */
     
    public Object getId();     
 
    
}
