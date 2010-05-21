package com.atomikos.datasource.xa.jdbc;
import javax.sql.XADataSource;

 /**
  *
  *
  *A test impl. of XATransactionalResource that works with the
  *XAResource stubs.
  */
  
public class TestJdbcTransactionalResource
extends JdbcTransactionalResource
{
  
    public TestJdbcTransactionalResource ( XADataSource xads , String name )
    {
        super ( name, xads  );
    } 
    
}
