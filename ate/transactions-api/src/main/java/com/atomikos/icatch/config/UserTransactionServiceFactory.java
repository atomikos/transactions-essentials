package com.atomikos.icatch.config;

import java.util.Properties;

/**
 *
 *
 *
 *A factory for UserTransactionService instances.
 *Each product will typically have its own implementation.
 *A system property can be used to indicate which one to use.
 *Implementations should have a public no-arg constructor!
 */

public interface UserTransactionServiceFactory
{
    /**
     * Gets the user transaction service instance.
     *
     * @return UserTransactionService A user handle that corresponds
     * to the underlying transaction service implementation.
     * @param properties The properties that can be used to initialize.
     */

    public UserTransactionService getUserTransactionService( Properties properties );
}
