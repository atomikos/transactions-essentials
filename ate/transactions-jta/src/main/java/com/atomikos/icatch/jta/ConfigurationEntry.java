package com.atomikos.icatch.jta;

import javax.transaction.xa.XAResource;

/**
 * 
 * 
 * A configuration entry consists of an XAResource and its name.
 */

class ConfigurationEntry
{
    private String name_;
    private XAResource xares_;

    ConfigurationEntry ( XAResource xares , String name )
    {
        xares_ = xares;
        name_ = name;
    }

    /**
     * Check for the name for the given resource.
     * 
     * @param xares
     *            The given XAResource
     * @return String The name, or null if wrong resource.
     */

    String getName ( XAResource xares )
    {
        try {
            if ( xares_.isSameRM ( xares ) )
                return name_;
            else
                return null;
        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () + " "
                    + e.getClass ().getName () );
        }
    }
}
