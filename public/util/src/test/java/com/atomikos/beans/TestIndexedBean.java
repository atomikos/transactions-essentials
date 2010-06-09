package com.atomikos.beans;

public class TestIndexedBean
implements java.io.Serializable
{
    private String[] values_;
    
    public TestIndexedBean()
    {
        values_ = new String[2];
        values_[0] = "Hello";
        values_[1] = "World"; 
        
    } 
    
    public String[] getMessages()
    {
        return values_; 
    }
    
    public void setMessages ( String[] msgs )
    {
      
        values_ = msgs; 
    }
    
    public String getMessages ( int i ) {
        return values_[i]; 
    }
    
    public void setMessages ( int i , String val ) 
    {
        values_[i] = val; 
    }
}
