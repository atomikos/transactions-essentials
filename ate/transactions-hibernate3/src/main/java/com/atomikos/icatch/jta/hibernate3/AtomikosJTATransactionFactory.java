package com.atomikos.icatch.jta.hibernate3;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.system.Configuration;

import org.hibernate.HibernateException;
import org.hibernate.transaction.JTATransactionFactory;

import javax.transaction.UserTransaction;
import java.util.Properties;

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

    private UserTransaction userTransaction;

    public void configure ( Properties props ) throws HibernateException {
    	
    	try {
			//fix for case 32252: hibernate config init - required for Hibernate 3.2.6 or lower!!!
			super.configure ( props );
		} catch ( Exception e ) {
			//fix for case 58114: exceptions here for Hibernate 3.2.7 and higher
			String msg = "Hibernate: error during config - ignore for hibernate 3.2.7 or higher";
			Configuration.logDebug ( msg , e );
		}
    }

    protected UserTransaction getUserTransaction() {
        if (this.userTransaction == null) {
            this.userTransaction = new UserTransactionImp();
        }
        return this.userTransaction;
    }
}
