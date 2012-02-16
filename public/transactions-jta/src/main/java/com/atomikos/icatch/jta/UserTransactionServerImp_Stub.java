/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.jta;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

public final class UserTransactionServerImp_Stub extends
        java.rmi.server.RemoteStub implements
        com.atomikos.icatch.jta.UserTransactionServer
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(UserTransactionServerImp_Stub.class);

    private static final java.rmi.server.Operation[] operations = {
            new java.rmi.server.Operation ( "java.lang.String begin(int)" ),
            new java.rmi.server.Operation ( "void commit(java.lang.String)" ),
            new java.rmi.server.Operation ( "int getStatus(java.lang.String)" ),
            new java.rmi.server.Operation ( "void rollback(java.lang.String)" ),
            new java.rmi.server.Operation (
                    "void setRollbackOnly(java.lang.String)" ) };

    private static final long interfaceHash = -8346227670383855650L;

    private static final long serialVersionUID = 2;

    private static boolean useNewInvoke;
    private static java.lang.reflect.Method $method_begin_0;
    private static java.lang.reflect.Method $method_commit_1;
    private static java.lang.reflect.Method $method_getStatus_2;
    private static java.lang.reflect.Method $method_rollback_3;
    private static java.lang.reflect.Method $method_setRollbackOnly_4;

    static {
        try {
            java.rmi.server.RemoteRef.class.getMethod ( "invoke",
                    new java.lang.Class[] { java.rmi.Remote.class,
                            java.lang.reflect.Method.class,
                            java.lang.Object[].class, long.class } );
            useNewInvoke = true;
            $method_begin_0 = com.atomikos.icatch.jta.UserTransactionServer.class
                    .getMethod ( "begin", new java.lang.Class[] { int.class } );
            $method_commit_1 = com.atomikos.icatch.jta.UserTransactionServer.class
                    .getMethod ( "commit",
                            new java.lang.Class[] { java.lang.String.class } );
            $method_getStatus_2 = com.atomikos.icatch.jta.UserTransactionServer.class
                    .getMethod ( "getStatus",
                            new java.lang.Class[] { java.lang.String.class } );
            $method_rollback_3 = com.atomikos.icatch.jta.UserTransactionServer.class
                    .getMethod ( "rollback",
                            new java.lang.Class[] { java.lang.String.class } );
            $method_setRollbackOnly_4 = com.atomikos.icatch.jta.UserTransactionServer.class
                    .getMethod ( "setRollbackOnly",
                            new java.lang.Class[] { java.lang.String.class } );
        } catch ( java.lang.NoSuchMethodException e ) {
            useNewInvoke = false;
        }
    }

    // constructors
    public UserTransactionServerImp_Stub ()
    {
        super ();
    }

    public UserTransactionServerImp_Stub ( java.rmi.server.RemoteRef ref )
    {
        super ( ref );
    }

    // methods from remote interfaces

    // implementation of begin(int)
    public java.lang.String begin ( int $param_int_1 )
            throws java.rmi.RemoteException,
            javax.transaction.NotSupportedException,
            javax.transaction.SystemException
    {
        try {
            if ( useNewInvoke ) {
                Object $result = ref.invoke ( this, $method_begin_0,
                        new java.lang.Object[] { new java.lang.Integer (
                                $param_int_1 ) }, 6953689522780412889L );
                return ((java.lang.String) $result);
            } else {
                java.rmi.server.RemoteCall call = ref.newCall (
                        (java.rmi.server.RemoteObject) this, operations, 0,
                        interfaceHash );
                try {
                    java.io.ObjectOutput out = call.getOutputStream ();
                    out.writeInt ( $param_int_1 );
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.MarshalException (
                            "error marshalling arguments", e );
                }
                ref.invoke ( call );
                java.lang.String $result;
                try {
                    java.io.ObjectInput in = call.getInputStream ();
                    $result = (java.lang.String) in.readObject ();
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.UnmarshalException (
                            "error unmarshalling return", e );
                } catch ( java.lang.ClassNotFoundException e ) {
                    throw new java.rmi.UnmarshalException (
                            "error unmarshalling return", e );
                } finally {
                    ref.done ( call );
                }
                return $result;
            }
        } catch ( java.lang.RuntimeException e ) {
            throw e;
        } catch ( java.rmi.RemoteException e ) {
            throw e;
        } catch ( javax.transaction.NotSupportedException e ) {
            throw e;
        } catch ( javax.transaction.SystemException e ) {
            throw e;
        } catch ( java.lang.Exception e ) {
            throw new java.rmi.UnexpectedException (
                    "undeclared checked exception", e );
        }
    }

    // implementation of commit(String)
    public void commit ( java.lang.String $param_String_1 )
            throws java.lang.IllegalStateException,
            java.lang.SecurityException, java.rmi.RemoteException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.RollbackException,
            javax.transaction.SystemException
    {
        try {
            if ( useNewInvoke ) {
                ref.invoke ( this, $method_commit_1,
                        new java.lang.Object[] { $param_String_1 },
                        -850594706682032390L );
            } else {
                java.rmi.server.RemoteCall call = ref.newCall (
                        (java.rmi.server.RemoteObject) this, operations, 1,
                        interfaceHash );
                try {
                    java.io.ObjectOutput out = call.getOutputStream ();
                    out.writeObject ( $param_String_1 );
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.MarshalException (
                            "error marshalling arguments", e );
                }
                ref.invoke ( call );
                ref.done ( call );
            }
        } catch ( java.lang.RuntimeException e ) {
            throw e;
        } catch ( java.rmi.RemoteException e ) {
            throw e;
        } catch ( javax.transaction.HeuristicMixedException e ) {
            throw e;
        } catch ( javax.transaction.HeuristicRollbackException e ) {
            throw e;
        } catch ( javax.transaction.RollbackException e ) {
            throw e;
        } catch ( javax.transaction.SystemException e ) {
            throw e;
        } catch ( java.lang.Exception e ) {
            throw new java.rmi.UnexpectedException (
                    "undeclared checked exception", e );
        }
    }

    // implementation of getStatus(String)
    public int getStatus ( java.lang.String $param_String_1 )
            throws java.rmi.RemoteException, javax.transaction.SystemException
    {
        try {
            if ( useNewInvoke ) {
                Object $result = ref.invoke ( this, $method_getStatus_2,
                        new java.lang.Object[] { $param_String_1 },
                        7487313509243286720L );
                return ((java.lang.Integer) $result).intValue ();
            } else {
                java.rmi.server.RemoteCall call = ref.newCall (
                        (java.rmi.server.RemoteObject) this, operations, 2,
                        interfaceHash );
                try {
                    java.io.ObjectOutput out = call.getOutputStream ();
                    out.writeObject ( $param_String_1 );
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.MarshalException (
                            "error marshalling arguments", e );
                }
                ref.invoke ( call );
                int $result;
                try {
                    java.io.ObjectInput in = call.getInputStream ();
                    $result = in.readInt ();
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.UnmarshalException (
                            "error unmarshalling return", e );
                } finally {
                    ref.done ( call );
                }
                return $result;
            }
        } catch ( java.lang.RuntimeException e ) {
            throw e;
        } catch ( java.rmi.RemoteException e ) {
            throw e;
        } catch ( javax.transaction.SystemException e ) {
            throw e;
        } catch ( java.lang.Exception e ) {
            throw new java.rmi.UnexpectedException (
                    "undeclared checked exception", e );
        }
    }

    // implementation of rollback(String)
    public void rollback ( java.lang.String $param_String_1 )
            throws java.lang.IllegalStateException,
            java.lang.SecurityException, java.rmi.RemoteException,
            javax.transaction.SystemException
    {
        try {
            if ( useNewInvoke ) {
                ref.invoke ( this, $method_rollback_3,
                        new java.lang.Object[] { $param_String_1 },
                        5718199897086415831L );
            } else {
                java.rmi.server.RemoteCall call = ref.newCall (
                        (java.rmi.server.RemoteObject) this, operations, 3,
                        interfaceHash );
                try {
                    java.io.ObjectOutput out = call.getOutputStream ();
                    out.writeObject ( $param_String_1 );
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.MarshalException (
                            "error marshalling arguments", e );
                }
                ref.invoke ( call );
                ref.done ( call );
            }
        } catch ( java.lang.RuntimeException e ) {
            throw e;
        } catch ( java.rmi.RemoteException e ) {
            throw e;
        } catch ( javax.transaction.SystemException e ) {
            throw e;
        } catch ( java.lang.Exception e ) {
            throw new java.rmi.UnexpectedException (
                    "undeclared checked exception", e );
        }
    }

    // implementation of setRollbackOnly(String)
    public void setRollbackOnly ( java.lang.String $param_String_1 )
            throws java.lang.IllegalStateException, java.rmi.RemoteException,
            javax.transaction.SystemException
    {
        try {
            if ( useNewInvoke ) {
                ref.invoke ( this, $method_setRollbackOnly_4,
                        new java.lang.Object[] { $param_String_1 },
                        -930710543198512812L );
            } else {
                java.rmi.server.RemoteCall call = ref.newCall (
                        (java.rmi.server.RemoteObject) this, operations, 4,
                        interfaceHash );
                try {
                    java.io.ObjectOutput out = call.getOutputStream ();
                    out.writeObject ( $param_String_1 );
                } catch ( java.io.IOException e ) {
                    throw new java.rmi.MarshalException (
                            "error marshalling arguments", e );
                }
                ref.invoke ( call );
                ref.done ( call );
            }
        } catch ( java.lang.RuntimeException e ) {
            throw e;
        } catch ( java.rmi.RemoteException e ) {
            throw e;
        } catch ( javax.transaction.SystemException e ) {
            throw e;
        } catch ( java.lang.Exception e ) {
            throw new java.rmi.UnexpectedException (
                    "undeclared checked exception", e );
        }
    }
}
