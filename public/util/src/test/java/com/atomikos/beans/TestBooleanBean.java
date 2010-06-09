package com.atomikos.beans;

public class TestBooleanBean
implements java.io.Serializable
{
    private boolean checked_;
    
    public TestBooleanBean() { checked_ = false; }
    
    public boolean isChecked()
    {
        return checked_; 
    } 
    
    public void setChecked ( boolean checked )
    {
        checked_ = checked; 
    }
}
