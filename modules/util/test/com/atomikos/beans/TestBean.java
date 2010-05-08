package com.atomikos.beans;
import java.io.Serializable;

/**
*A test bean to check if Beans.instantiate works as expected.
*/

public class TestBean
implements Serializable
{
    private String string;
    private boolean bool;
    private int integer;
    private short shortInt;
    private long longInt;
    private float floatNum;
    private double doubleNum;
    private String[] indexedString;
    

    public TestBean ()
    {
        indexedString = new String[] { "Hello" , "World" };
        
    }


    public void setNoGetterProperty ( String value ) {}
    public String getString() { return string; }
    public void setString ( String string ) { this.string = string; }

    public int getInt() { return integer; }
    public void setInt ( int integer ) { this.integer = integer; }

    public boolean isBoolean() { return bool; }
    public void setBoolean ( boolean bool ) { this.bool = bool; }

    public long getLong() { return longInt; }
    public void setLong ( long longInt ) { this.longInt = longInt; }

    public short getShort() { return shortInt; }
    public void setShort ( short shortInt ) { this.shortInt = shortInt; }

    public float getFloat() { return floatNum; }
    public void setFloat ( float floatNum ) { this.floatNum = floatNum; }

    public double getDouble() { return doubleNum; }
    public void setDouble ( double doubleNum ) { this.doubleNum = doubleNum; }

    public Object getErrorIfYouSeeThis() { return new Object(); }
    public void setErrorIfYouSeeThis ( Object error ) { }

    public String[] getIndexedString() { return indexedString; }
    public void setIndexedString ( String[] value ) { indexedString = value; }
    public String getIndexedString ( int i ) { return indexedString[i]; }
    public void setIndexedString ( int i , String value ) { indexedString[i] = value; }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append ( "string: " + getString() );
        buf.append ( " int: " + getInt() );
        buf.append ( " boolean: " + isBoolean() );
        buf.append ( " long: " + getLong() );
        buf.append ( " short: " + getShort() );
        buf.append ( " float: " + getFloat() );
        buf.append ( " double: " + getDouble() );
        return buf.toString();
    }
}

