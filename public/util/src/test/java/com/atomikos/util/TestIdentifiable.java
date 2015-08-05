package com.atomikos.util;

public class TestIdentifiable implements Identifiable
{
    protected Object id_ ;

    public TestIdentifiable()
    {
        id_ = new Object();
    }

    public Object getId()
    {
        return id_;
    }
}
