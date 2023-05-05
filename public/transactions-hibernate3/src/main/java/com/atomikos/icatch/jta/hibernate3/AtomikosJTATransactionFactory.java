/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate3;

import java.util.Properties;

import javax.transaction.UserTransaction;

import org.hibernate.HibernateException;
import org.hibernate.transaction.JTATransactionFactory;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Atomikos-specific JTATransactionFactory implementation that does not
 * rely on JNDI for standalone (JNDI-less) deployments.
 *
 * <p>To use Atomikos as the Hibernate JTA transaction manager,
 * specify this class as the value of the 
 * <b>hibernate.transaction.factory_class</b> of the
 * hibernate configuration properties.</p>
 * 
 * 
 * @author Les Hazlewood
 * @author Ludovic Orban
 */
public class AtomikosJTATransactionFactory extends JTATransactionFactory {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJTATransactionFactory.class);

    private UserTransaction userTransaction;

    public void configure ( Properties props ) throws HibernateException {
    	
    	try {
			//fix for case 32252: hibernate config init - required for Hibernate 3.2.6 or lower!!!
			super.configure ( props );
		} catch ( Exception e ) {
			//fix for case 58114: exceptions here for Hibernate 3.2.7 and higher
			String msg = "Hibernate: error during config - ignore for hibernate 3.2.7 or higher";
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( msg , e );
		}
    }

    protected UserTransaction getUserTransaction() {
        if (this.userTransaction == null) {
            this.userTransaction = new UserTransactionImp();
        }
        return this.userTransaction;
    }
}
