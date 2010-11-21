package com.atomikos.icatch.jta.hibernate3.mock;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 *
 *
 *A dummy test class for a SQL Connection.
 *Most of the method invocations will
 *throw a SQLException.
 */
 
 public class TestConnection implements Connection
{
   
	static final String SQL_ERROR_MESSAGE = "Not implemented";

	public static final int DEFAULT_ISOLATION_LEVEL =  - 2;

	public static final int UNSUPPORTED_ISOLATION_LEVEL = Integer.MAX_VALUE;
	 
    private TestXAConnection xaConn_;
    
    private boolean closed_;

	private boolean autocommitState = true;
    
	private int isolationLevel = DEFAULT_ISOLATION_LEVEL;
	
    public TestConnection ( TestXAConnection xaConn ) {
          xaConn_ = xaConn;
          closed_ = false;
    }
    
    private void error ( String msg ) throws SQLException 
    {
       SQLException e = new SQLException ( msg );
       xaConn_.connectionError ( e );
       throw e;
    }
    
    public Statement createStatement() throws SQLException
    {
        	return new TestStatement();
    }
    
    public PreparedStatement prepareStatement ( String sql ) 
    throws SQLException
    {
	error ( SQL_ERROR_MESSAGE );
            return null;

    }
    
    public CallableStatement prepareCall ( String sql ) throws SQLException {
	error ( SQL_ERROR_MESSAGE );
            return null;

    }
    
   
    public String nativeSQL ( String sql ) throws SQLException {
	
	error ( SQL_ERROR_MESSAGE );
            return null;

    }
    
    public void setAutoCommit(boolean autoCommit) throws SQLException {
    	autocommitState = autoCommit;
    }
    
    public boolean getAutoCommit() throws SQLException {
              return autocommitState ;
    }
    
    public void commit() throws SQLException {
	error ( SQL_ERROR_MESSAGE );
    }
    
    public void rollback() throws SQLException {
	error ( SQL_ERROR_MESSAGE );
    }
    
    public void close() throws SQLException {
       xaConn_.connectionClosed();
       closed_ = true;
    }
    
    
    
    public boolean isClosed() throws SQLException {
	return closed_;
  
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
	return null;
  
    }
    
    public void setReadOnly(boolean readOnly) throws SQLException {
	error ( SQL_ERROR_MESSAGE );
    }
    
    public boolean isReadOnly() throws SQLException {
          error ( "Not implemented" );  
          return false;
    }
    
    public void setCatalog(String catalog) throws SQLException {
	error ( SQL_ERROR_MESSAGE );
    }
    
    public String getCatalog() throws SQLException {
          error ( SQL_ERROR_MESSAGE );
          return null;
    }
    
    public void setTransactionIsolation(int level) throws SQLException {
    	if ( level == UNSUPPORTED_ISOLATION_LEVEL ) throw new SQLException ( "Unsupported isolation level: " + level );
    	this.isolationLevel = level;
    }
    
    public int getTransactionIsolation() throws SQLException{
		
              return isolationLevel;
    }
    
    public SQLWarning getWarnings() throws SQLException {
	error ( SQL_ERROR_MESSAGE );
              return null;
  
    }
    
    public void clearWarnings() throws SQLException {
	error ( SQL_ERROR_MESSAGE );
    }
    
    public Statement createStatement ( int resultSetType, 
                                                      int resultSetConcurrency )
                                                      throws SQLException
    {
        error ( SQL_ERROR_MESSAGE );
        return null;
    }
    
    public PreparedStatement prepareStatement(String sql , 
        int resultSetType, int resultSetConcurrency ) throws SQLException
    {
	error ( SQL_ERROR_MESSAGE );
              return null;
    }
    
    
    public CallableStatement prepareCall( String sql,
            int resultSetType, int resultSetConcurrency ) throws SQLException 
    {
	error ( SQL_ERROR_MESSAGE );
              return null;
    }
    
    
    public Map getTypeMap()
               throws SQLException
    {
        error ( SQL_ERROR_MESSAGE );
        return null;
    }
    
    public void setTypeMap ( Map map ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    }
    
    public void setHoldability ( int value ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    }
    
    public int getHoldability() throws SQLException

    {
    	error ( SQL_ERROR_MESSAGE );
    	return -1;
    }
    
    public Savepoint setSavepoint() throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public Savepoint setSavepoint ( String name ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public void rollback ( Savepoint point ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    }
    
    public void releaseSavepoint ( Savepoint point ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    }
    
    public Statement createStatement ( int p , int q , int r ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public PreparedStatement prepareStatement ( String sql , int p , int q , int r ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public PreparedStatement prepareStatement ( String sql , int p ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public PreparedStatement prepareStatement ( String sql , int[] p ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public PreparedStatement prepareStatement ( String sql , String[] p ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public CallableStatement prepareCall ( String sql , int p , int q , int r ) throws SQLException
    {
    	error ( SQL_ERROR_MESSAGE );
    	return null;
    }
    
    public boolean equals ( Object o )
    {
        boolean ret = false;
        if ( o instanceof TestConnection ) {
            TestConnection other = ( TestConnection ) o;
            ret = other.xaConn_.equals ( this.xaConn_ );
        } 
        
        return ret;
    }
    
    public int hashCode()
    {
        if ( xaConn_ != null ) return xaConn_.toString().hashCode(); 
        else return super.hashCode();
    }


   
}


