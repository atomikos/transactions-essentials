package com.atomikos.icatch.jta;

import java.rmi.RemoteException;
import java.rmi.UnexpectedException;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class _UserTransactionServer_Stub extends Stub implements
        UserTransactionServer
{

    private static final String[] _type_ids = { "RMI:com.atomikos.icatch.jta.UserTransactionServer:0000000000000000" };

    public String[] _ids ()
    {
        return _type_ids;
    }

    public String begin ( int arg0 ) throws RemoteException,
            javax.transaction.SystemException, NotSupportedException
    {
        if ( !Util.isLocal ( this ) ) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    OutputStream out = _request ( "begin", true );
                    out.write_long ( arg0 );
                    in = (org.omg.CORBA_2_3.portable.InputStream) _invoke ( out );
                    return (String) in.read_value ( String.class );
                } catch ( ApplicationException ex ) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex
                            .getInputStream ();
                    String id = in.read_string ();
                    if ( id.equals ( "IDL:javax/transaction/SystemEx:1.0" ) ) {
                        throw (javax.transaction.SystemException) in
                                .read_value ( javax.transaction.SystemException.class );
                    }
                    if ( id
                            .equals ( "IDL:javax/transaction/NotSupportedEx:1.0" ) ) {
                        throw (NotSupportedException) in
                                .read_value ( NotSupportedException.class );
                    }
                    throw new UnexpectedException ( id );
                } catch ( RemarshalException ex ) {
                    return begin ( arg0 );
                } finally {
                    _releaseReply ( in );
                }
            } catch ( SystemException ex ) {
                throw Util.mapSystemException ( ex );
            }
        } else {
            ServantObject so = _servant_preinvoke ( "begin",
                    UserTransactionServer.class );
            if ( so == null ) {
                return begin ( arg0 );
            }
            try {
                return ((UserTransactionServer) so.servant).begin ( arg0 );
            } catch ( Throwable ex ) {
                Throwable exCopy = (Throwable) Util.copyObject ( ex, _orb () );
                if ( exCopy instanceof javax.transaction.SystemException ) {
                    throw (javax.transaction.SystemException) exCopy;
                }
                if ( exCopy instanceof NotSupportedException ) {
                    throw (NotSupportedException) exCopy;
                }
                throw Util.wrapException ( exCopy );
            } finally {
                _servant_postinvoke ( so );
            }
        }
    }

    public void commit ( String arg0 ) throws RemoteException,
            RollbackException, HeuristicMixedException,
            HeuristicRollbackException, javax.transaction.SystemException
    {
        if ( !Util.isLocal ( this ) ) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) _request (
                            "commit", true );
                    out.write_value ( arg0, String.class );
                    _invoke ( out );
                } catch ( ApplicationException ex ) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex
                            .getInputStream ();
                    String id = in.read_string ();
                    if ( id.equals ( "IDL:javax/transaction/RollbackEx:1.0" ) ) {
                        throw (RollbackException) in
                                .read_value ( RollbackException.class );
                    }
                    if ( id
                            .equals ( "IDL:javax/transaction/HeuristicMixedEx:1.0" ) ) {
                        throw (HeuristicMixedException) in
                                .read_value ( HeuristicMixedException.class );
                    }
                    if ( id
                            .equals ( "IDL:javax/transaction/HeuristicRollbackEx:1.0" ) ) {
                        throw (HeuristicRollbackException) in
                                .read_value ( HeuristicRollbackException.class );
                    }
                    if ( id.equals ( "IDL:javax/transaction/SystemEx:1.0" ) ) {
                        throw (javax.transaction.SystemException) in
                                .read_value ( javax.transaction.SystemException.class );
                    }
                    throw new UnexpectedException ( id );
                } catch ( RemarshalException ex ) {
                    commit ( arg0 );
                } finally {
                    _releaseReply ( in );
                }
            } catch ( SystemException ex ) {
                throw Util.mapSystemException ( ex );
            }
        } else {
            ServantObject so = _servant_preinvoke ( "commit",
                    UserTransactionServer.class );
            if ( so == null ) {
                commit ( arg0 );
                return;
            }
            try {
                ((UserTransactionServer) so.servant).commit ( arg0 );
            } catch ( Throwable ex ) {
                Throwable exCopy = (Throwable) Util.copyObject ( ex, _orb () );
                if ( exCopy instanceof RollbackException ) {
                    throw (RollbackException) exCopy;
                }
                if ( exCopy instanceof HeuristicMixedException ) {
                    throw (HeuristicMixedException) exCopy;
                }
                if ( exCopy instanceof HeuristicRollbackException ) {
                    throw (HeuristicRollbackException) exCopy;
                }
                if ( exCopy instanceof javax.transaction.SystemException ) {
                    throw (javax.transaction.SystemException) exCopy;
                }
                throw Util.wrapException ( exCopy );
            } finally {
                _servant_postinvoke ( so );
            }
        }
    }

    public void rollback ( String arg0 ) throws RemoteException,
            javax.transaction.SystemException
    {
        if ( !Util.isLocal ( this ) ) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) _request (
                            "rollback", true );
                    out.write_value ( arg0, String.class );
                    _invoke ( out );
                } catch ( ApplicationException ex ) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex
                            .getInputStream ();
                    String id = in.read_string ();
                    if ( id.equals ( "IDL:javax/transaction/SystemEx:1.0" ) ) {
                        throw (javax.transaction.SystemException) in
                                .read_value ( javax.transaction.SystemException.class );
                    }
                    throw new UnexpectedException ( id );
                } catch ( RemarshalException ex ) {
                    rollback ( arg0 );
                } finally {
                    _releaseReply ( in );
                }
            } catch ( SystemException ex ) {
                throw Util.mapSystemException ( ex );
            }
        } else {
            ServantObject so = _servant_preinvoke ( "rollback",
                    UserTransactionServer.class );
            if ( so == null ) {
                rollback ( arg0 );
                return;
            }
            try {
                ((UserTransactionServer) so.servant).rollback ( arg0 );
            } catch ( Throwable ex ) {
                Throwable exCopy = (Throwable) Util.copyObject ( ex, _orb () );
                if ( exCopy instanceof javax.transaction.SystemException ) {
                    throw (javax.transaction.SystemException) exCopy;
                }
                throw Util.wrapException ( exCopy );
            } finally {
                _servant_postinvoke ( so );
            }
        }
    }

    public void setRollbackOnly ( String arg0 ) throws RemoteException,
            javax.transaction.SystemException
    {
        if ( !Util.isLocal ( this ) ) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) _request (
                            "setRollbackOnly", true );
                    out.write_value ( arg0, String.class );
                    _invoke ( out );
                } catch ( ApplicationException ex ) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex
                            .getInputStream ();
                    String id = in.read_string ();
                    if ( id.equals ( "IDL:javax/transaction/SystemEx:1.0" ) ) {
                        throw (javax.transaction.SystemException) in
                                .read_value ( javax.transaction.SystemException.class );
                    }
                    throw new UnexpectedException ( id );
                } catch ( RemarshalException ex ) {
                    setRollbackOnly ( arg0 );
                } finally {
                    _releaseReply ( in );
                }
            } catch ( SystemException ex ) {
                throw Util.mapSystemException ( ex );
            }
        } else {
            ServantObject so = _servant_preinvoke ( "setRollbackOnly",
                    UserTransactionServer.class );
            if ( so == null ) {
                setRollbackOnly ( arg0 );
                return;
            }
            try {
                ((UserTransactionServer) so.servant).setRollbackOnly ( arg0 );
            } catch ( Throwable ex ) {
                Throwable exCopy = (Throwable) Util.copyObject ( ex, _orb () );
                if ( exCopy instanceof javax.transaction.SystemException ) {
                    throw (javax.transaction.SystemException) exCopy;
                }
                throw Util.wrapException ( exCopy );
            } finally {
                _servant_postinvoke ( so );
            }
        }
    }

    public int getStatus ( String arg0 ) throws RemoteException,
            javax.transaction.SystemException
    {
        if ( !Util.isLocal ( this ) ) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = (org.omg.CORBA_2_3.portable.OutputStream) _request (
                            "getStatus", true );
                    out.write_value ( arg0, String.class );
                    in = (org.omg.CORBA_2_3.portable.InputStream) _invoke ( out );
                    return in.read_long ();
                } catch ( ApplicationException ex ) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex
                            .getInputStream ();
                    String id = in.read_string ();
                    if ( id.equals ( "IDL:javax/transaction/SystemEx:1.0" ) ) {
                        throw (javax.transaction.SystemException) in
                                .read_value ( javax.transaction.SystemException.class );
                    }
                    throw new UnexpectedException ( id );
                } catch ( RemarshalException ex ) {
                    return getStatus ( arg0 );
                } finally {
                    _releaseReply ( in );
                }
            } catch ( SystemException ex ) {
                throw Util.mapSystemException ( ex );
            }
        } else {
            ServantObject so = _servant_preinvoke ( "getStatus",
                    UserTransactionServer.class );
            if ( so == null ) {
                return getStatus ( arg0 );
            }
            try {
                return ((UserTransactionServer) so.servant).getStatus ( arg0 );
            } catch ( Throwable ex ) {
                Throwable exCopy = (Throwable) Util.copyObject ( ex, _orb () );
                if ( exCopy instanceof javax.transaction.SystemException ) {
                    throw (javax.transaction.SystemException) exCopy;
                }
                throw Util.wrapException ( exCopy );
            } finally {
                _servant_postinvoke ( so );
            }
        }
    }
}
