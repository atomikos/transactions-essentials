/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.rmi.Remote;

import javax.rmi.CORBA.Tie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class _UserTransactionServerImp_Tie extends ObjectImpl implements Tie
{

    private UserTransactionServerImp target = null;

    private static final String[] _type_ids = { "RMI:com.atomikos.icatch.jta.UserTransactionServer:0000000000000000" };

    public void setTarget ( Remote target )
    {
        this.target = (UserTransactionServerImp) target;
    }

    public Remote getTarget ()
    {
        return target;
    }

    public org.omg.CORBA.Object thisObject ()
    {
        return this;
    }

    public void deactivate ()
    {
        _orb ().disconnect ( this );
        _set_delegate ( null );
        target = null;
    }

    public ORB orb ()
    {
        return _orb ();
    }

    public void orb ( ORB orb )
    {
        orb.connect ( this );
    }

    public String[] _ids ()
    {
        return _type_ids;
    }

    public OutputStream _invoke ( String method , InputStream _in ,
            ResponseHandler reply ) throws SystemException
    {
        try {
            org.omg.CORBA_2_3.portable.InputStream in = (org.omg.CORBA_2_3.portable.InputStream) _in;
            switch ( method.length () ) {
            case 5:
                if ( method.equals ( "begin" ) ) {
                    int arg0 = in.read_long ();
                    String result;
                    try {
                        result = target.begin ( arg0 );
                    } catch ( javax.transaction.SystemException ex ) {
                        String id = "IDL:javax/transaction/SystemEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex,
                                javax.transaction.SystemException.class );
                        return out;
                    } catch ( NotSupportedException ex ) {
                        String id = "IDL:javax/transaction/NotSupportedEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex, NotSupportedException.class );
                        return out;
                    }
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                            .createReply ();
                    out.write_value ( result, String.class );
                    return out;
                }
            case 6:
                if ( method.equals ( "commit" ) ) {
                    String arg0 = (String) in.read_value ( String.class );
                    try {
                        target.commit ( arg0 );
                    } catch ( RollbackException ex ) {
                        String id = "IDL:javax/transaction/RollbackEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex, RollbackException.class );
                        return out;
                    } catch ( HeuristicMixedException ex ) {
                        String id = "IDL:javax/transaction/HeuristicMixedEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex, HeuristicMixedException.class );
                        return out;
                    } catch ( HeuristicRollbackException ex ) {
                        String id = "IDL:javax/transaction/HeuristicRollbackEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex, HeuristicRollbackException.class );
                        return out;
                    } catch ( javax.transaction.SystemException ex ) {
                        String id = "IDL:javax/transaction/SystemEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex,
                                javax.transaction.SystemException.class );
                        return out;
                    }
                    OutputStream out = reply.createReply ();
                    return out;
                }
            case 8:
                if ( method.equals ( "rollback" ) ) {
                    String arg0 = (String) in.read_value ( String.class );
                    try {
                        target.rollback ( arg0 );
                    } catch ( javax.transaction.SystemException ex ) {
                        String id = "IDL:javax/transaction/SystemEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex,
                                javax.transaction.SystemException.class );
                        return out;
                    }
                    OutputStream out = reply.createReply ();
                    return out;
                }
            case 9:
                if ( method.equals ( "getStatus" ) ) {
                    String arg0 = (String) in.read_value ( String.class );
                    int result;
                    try {
                        result = target.getStatus ( arg0 );
                    } catch ( javax.transaction.SystemException ex ) {
                        String id = "IDL:javax/transaction/SystemEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex,
                                javax.transaction.SystemException.class );
                        return out;
                    }
                    OutputStream out = reply.createReply ();
                    out.write_long ( result );
                    return out;
                }
            case 15:
                if ( method.equals ( "setRollbackOnly" ) ) {
                    String arg0 = (String) in.read_value ( String.class );
                    try {
                        target.setRollbackOnly ( arg0 );
                    } catch ( javax.transaction.SystemException ex ) {
                        String id = "IDL:javax/transaction/SystemEx:1.0";
                        org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) reply
                                .createExceptionReply ();
                        out.write_string ( id );
                        out.write_value ( ex,
                                javax.transaction.SystemException.class );
                        return out;
                    }
                    OutputStream out = reply.createReply ();
                    return out;
                }
            }
            throw new BAD_OPERATION ();
        } catch ( SystemException ex ) {
            throw ex;
        } catch ( Throwable ex ) {
            throw new UnknownException ( ex );
        }
    }
}
