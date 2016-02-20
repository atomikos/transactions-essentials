/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;


@SuppressWarnings("deprecation")
public final class UserTransactionServerImp_Skel implements
        java.rmi.server.Skeleton
{

    private static final java.rmi.server.Operation[] operations = {
            new java.rmi.server.Operation ( "java.lang.String begin(int)" ),
            new java.rmi.server.Operation ( "void commit(java.lang.String)" ),
            new java.rmi.server.Operation ( "int getStatus(java.lang.String)" ),
            new java.rmi.server.Operation ( "void rollback(java.lang.String)" ),
            new java.rmi.server.Operation (
                    "void setRollbackOnly(java.lang.String)" ) };

    private static final long interfaceHash = -8346227670383855650L;

    public java.rmi.server.Operation[] getOperations ()
    {
        return (java.rmi.server.Operation[]) operations.clone ();
    }

    public void dispatch ( java.rmi.Remote obj ,
            java.rmi.server.RemoteCall call , int opnum , long hash )
            throws java.lang.Exception
    {
        if ( opnum < 0 ) {
            if ( hash == 6953689522780412889L ) {
                opnum = 0;
            } else if ( hash == -850594706682032390L ) {
                opnum = 1;
            } else if ( hash == 7487313509243286720L ) {
                opnum = 2;
            } else if ( hash == 5718199897086415831L ) {
                opnum = 3;
            } else if ( hash == -930710543198512812L ) {
                opnum = 4;
            } else {
                throw new java.rmi.UnmarshalException ( "invalid method hash" );
            }
        } else {
            if ( hash != interfaceHash )
                throw new java.rmi.server.SkeletonMismatchException (
                        "interface hash mismatch" );
        }

        com.atomikos.icatch.jta.UserTransactionServerImp server = (com.atomikos.icatch.jta.UserTransactionServerImp) obj;
        switch ( opnum ) {
        case 0: // begin(int)
        {
            int $param_int_1;
            try {
                java.io.ObjectInput in = call.getInputStream ();
                $param_int_1 = in.readInt ();
            } catch ( java.io.IOException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } finally {
                call.releaseInputStream ();
            }
            java.lang.String $result = server.begin ( $param_int_1 );
            try {
                java.io.ObjectOutput out = call.getResultStream ( true );
                out.writeObject ( $result );
            } catch ( java.io.IOException e ) {
                throw new java.rmi.MarshalException (
                        "error marshalling return", e );
            }
            break;
        }

        case 1: // commit(String)
        {
            java.lang.String $param_String_1;
            try {
                java.io.ObjectInput in = call.getInputStream ();
                $param_String_1 = (java.lang.String) in.readObject ();
            } catch ( java.io.IOException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } catch ( java.lang.ClassNotFoundException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } finally {
                call.releaseInputStream ();
            }
            server.commit ( $param_String_1 );
            try {
                call.getResultStream ( true );
            } catch ( java.io.IOException e ) {
                throw new java.rmi.MarshalException (
                        "error marshalling return", e );
            }
            break;
        }

        case 2: // getStatus(String)
        {
            java.lang.String $param_String_1;
            try {
                java.io.ObjectInput in = call.getInputStream ();
                $param_String_1 = (java.lang.String) in.readObject ();
            } catch ( java.io.IOException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } catch ( java.lang.ClassNotFoundException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } finally {
                call.releaseInputStream ();
            }
            int $result = server.getStatus ( $param_String_1 );
            try {
                java.io.ObjectOutput out = call.getResultStream ( true );
                out.writeInt ( $result );
            } catch ( java.io.IOException e ) {
                throw new java.rmi.MarshalException (
                        "error marshalling return", e );
            }
            break;
        }

        case 3: // rollback(String)
        {
            java.lang.String $param_String_1;
            try {
                java.io.ObjectInput in = call.getInputStream ();
                $param_String_1 = (java.lang.String) in.readObject ();
            } catch ( java.io.IOException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } catch ( java.lang.ClassNotFoundException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } finally {
                call.releaseInputStream ();
            }
            server.rollback ( $param_String_1 );
            try {
                call.getResultStream ( true );
            } catch ( java.io.IOException e ) {
                throw new java.rmi.MarshalException (
                        "error marshalling return", e );
            }
            break;
        }

        case 4: // setRollbackOnly(String)
        {
            java.lang.String $param_String_1;
            try {
                java.io.ObjectInput in = call.getInputStream ();
                $param_String_1 = (java.lang.String) in.readObject ();
            } catch ( java.io.IOException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } catch ( java.lang.ClassNotFoundException e ) {
                throw new java.rmi.UnmarshalException (
                        "error unmarshalling arguments", e );
            } finally {
                call.releaseInputStream ();
            }
            server.setRollbackOnly ( $param_String_1 );
            try {
                call.getResultStream ( true );
            } catch ( java.io.IOException e ) {
                throw new java.rmi.MarshalException (
                        "error marshalling return", e );
            }
            break;
        }

        default:
            throw new java.rmi.UnmarshalException ( "invalid method number" );
        }
    }
}
