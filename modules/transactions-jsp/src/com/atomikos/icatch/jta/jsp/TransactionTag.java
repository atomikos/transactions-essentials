package com.atomikos.icatch.jta.jsp;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * A JSP tag handler for transaction demarcation in a JSP page.
 */




public final class TransactionTag extends BodyTagSupport 
implements TryCatchFinally
{
	//private static final String DEFAULT_UTX_NAME = "java:comp/UserTransaction";
	
	private UserTransaction userTransaction;
	
	private int timeout;
	
	//private String userTransactionName;

    /**
     * 
     */
    
    public TransactionTag()
    {
        super();
        timeout = -1;
        //userTransactionName = DEFAULT_UTX_NAME;    
    }
    
    /**
     * Set the timeout for new transactions. Specified as an attribute
     * in the JSP opening tag.
     * 
     * @param seconds The timeout in seconds.
     */
    
    public void setTimeout ( String seconds )
	{
		timeout = Integer.parseInt ( seconds ); 
	}
	
//	/**
//	 * Set the JNDI name of the UserTransaction instance.
//	 * Specified as an attribute in the JSP opening tag.
//	 * This is useful if the web container has a different
//	 * installation name in JNDI. Applications (JSP pages) 
//	 * should use initialization parameters to configure
//	 * this value in an installation-specific way.
//	 * 
//	 * @param name The name (in JNDI) where the UserTransaction 
//	 * can be found.
//	 */
//	
//	private void setUserTransactionName ( String name )
//	{
//		userTransactionName = name;
//	}
	
	/**
	 * Invoked before the body is evaluated.
	 * This method will start a new transaction.
	 * @exception JspException On failure.
	 */
	
 	public int doStartTag() throws JspException
 	{
// 		try {
//			String name = pageContext.getServletContext().
//				getInitParameter( "javax.transaction.UserTransaction");
//				
//			if ( name != null ) setUserTransactionName ( name );	
// 		}
// 		catch ( Exception e ) {
// 			throw new JspTagException ( e.getMessage() );
// 		}
 		
//		try {
//			javax.naming.Context ctx = new javax.naming.InitialContext();
//			userTransaction = ( UserTransaction ) ctx.lookup ( userTransactionName );
//		}
//		catch ( javax.naming.NamingException e ) {
//			throw new JspTagException ( 
//			"TransactionTag: could not find UserTransaction. " +
//			"Please make sure that " + userTransactionName + " exists in JNDI and "+
//			"set context init parameter 'javax.transaction.UserTransaction' " +
//			"to the JNDI name if different from " + DEFAULT_UTX_NAME + " " +
//			e.getMessage() );
//		}

		userTransaction = new UserTransactionImp();
        
		try {
			if ( timeout >= 0 ) userTransaction.setTransactionTimeout ( timeout );
			userTransaction.begin();
		}
		catch ( Exception e ) {
			throw new JspTagException ( "Error starting transaction: " + e.getMessage() );
		}
		
		return EVAL_BODY_INCLUDE;
 	}

    /**
     * This method is invoked by the JSP engine whenever there
     * is an exception in the body. In this case, the 
     * transaction will be marked for rollback.
     * 
     * @param arg The exception that has happened. This
     * exception is re-raised after marking the transaction
     * for rollback.
     */
    
    public void doCatch(Throwable arg) throws Throwable
    {
        //mark transaction for rollback
        if ( userTransaction != null ) userTransaction.setRollbackOnly();
		throw arg;
    }

    /**
     * Invoked at the end of the tag.
     * This method will try to commit the transaction.
     * If any exception happened, then the transaction
     * will have been marked for rollback so 
     * commit will fail.
     */
    
    public void doFinally()
    {
    	try {
			userTransaction.commit();
    	}
    	catch ( Exception e ) {
    		//ignore: happens if setRollbackOnly
    		//this method should not throw any exceptions any more
    		//according to the specs
    	}
		
    }

}
