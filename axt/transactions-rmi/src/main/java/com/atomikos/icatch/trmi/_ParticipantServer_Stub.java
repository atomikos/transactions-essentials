package com.atomikos.icatch.trmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.util.Dictionary;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RollbackException;


public class _ParticipantServer_Stub extends Stub implements ParticipantServer {
    
    private static final String[] _type_ids = {
        "RMI:com.atomikos.icatch.trmi.ParticipantServer:0000000000000000"
    };
    
    public String[] _ids() { 
        return _type_ids;
    }
    
    public HeuristicMessage[] commit(String arg0) throws HeurHazardException, HeurMixedException, HeurRollbackException, RollbackException, RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("commit", true);
                    out.write_value(arg0,String.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return (HeuristicMessage[]) in.read_value(HeuristicMessage[].class);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    if (id.equals("IDL:com/atomikos/icatch/HeurHazardEx:1.0")) {
                        throw (HeurHazardException) in.read_value(HeurHazardException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurMixedEx:1.0")) {
                        throw (HeurMixedException) in.read_value(HeurMixedException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurRollbackEx:1.0")) {
                        throw (HeurRollbackException) in.read_value(HeurRollbackException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/RollbackEx:1.0")) {
                        throw (RollbackException) in.read_value(RollbackException.class);
                    }
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return commit(arg0);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("commit",ParticipantServer.class);
            if (so == null) {
                return commit(arg0);
            }
            try {
                HeuristicMessage[] result = ((ParticipantServer)so.servant).commit(arg0);
                return (HeuristicMessage[])Util.copyObject(result,_orb());
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                if (exCopy instanceof HeurHazardException) {
                    throw (HeurHazardException)exCopy;
                }
                if (exCopy instanceof HeurMixedException) {
                    throw (HeurMixedException)exCopy;
                }
                if (exCopy instanceof HeurRollbackException) {
                    throw (HeurRollbackException)exCopy;
                }
                if (exCopy instanceof RollbackException) {
                    throw (RollbackException)exCopy;
                }
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
    
    public HeuristicMessage[] commitOnePhase(String arg0, int arg1, Dictionary arg2) throws HeurHazardException, HeurMixedException, HeurRollbackException, RollbackException, RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("commitOnePhase", true);
                    out.write_value(arg0,String.class);
                    out.write_long(arg1);
                    out.write_value((Serializable)arg2,Dictionary.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return (HeuristicMessage[]) in.read_value(HeuristicMessage[].class);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    if (id.equals("IDL:com/atomikos/icatch/HeurHazardEx:1.0")) {
                        throw (HeurHazardException) in.read_value(HeurHazardException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurMixedEx:1.0")) {
                        throw (HeurMixedException) in.read_value(HeurMixedException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurRollbackEx:1.0")) {
                        throw (HeurRollbackException) in.read_value(HeurRollbackException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/RollbackEx:1.0")) {
                        throw (RollbackException) in.read_value(RollbackException.class);
                    }
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return commitOnePhase(arg0,arg1,arg2);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("commitOnePhase",ParticipantServer.class);
            if (so == null) {
                return commitOnePhase(arg0, arg1, arg2);
            }
            try {
                Object[] copies = Util.copyObjects(new Object[]{arg0,arg2},_orb());
                String arg0Copy = (String) copies[0];
                Dictionary arg2Copy = (Dictionary) copies[1];
                HeuristicMessage[] result = ((ParticipantServer)so.servant).commitOnePhase(arg0Copy, arg1, arg2Copy);
                return (HeuristicMessage[])Util.copyObject(result,_orb());
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                if (exCopy instanceof HeurHazardException) {
                    throw (HeurHazardException)exCopy;
                }
                if (exCopy instanceof HeurMixedException) {
                    throw (HeurMixedException)exCopy;
                }
                if (exCopy instanceof HeurRollbackException) {
                    throw (HeurRollbackException)exCopy;
                }
                if (exCopy instanceof RollbackException) {
                    throw (RollbackException)exCopy;
                }
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
    
    public void forget(String arg0) throws RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("forget", true);
                    out.write_value(arg0,String.class);
                    _invoke(out);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    forget(arg0);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("forget",ParticipantServer.class);
            if (so == null) {
                forget(arg0);
                return ;
            }
            try {
                ((ParticipantServer)so.servant).forget(arg0);
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
    
    public int prepare(String arg0, int arg1, Dictionary arg2) throws HeurHazardException, HeurMixedException, RollbackException, RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("prepare", true);
                    out.write_value(arg0,String.class);
                    out.write_long(arg1);
                    out.write_value((Serializable)arg2,Dictionary.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return in.read_long();
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    if (id.equals("IDL:com/atomikos/icatch/HeurHazardEx:1.0")) {
                        throw (HeurHazardException) in.read_value(HeurHazardException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurMixedEx:1.0")) {
                        throw (HeurMixedException) in.read_value(HeurMixedException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/RollbackEx:1.0")) {
                        throw (RollbackException) in.read_value(RollbackException.class);
                    }
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return prepare(arg0,arg1,arg2);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("prepare",ParticipantServer.class);
            if (so == null) {
                return prepare(arg0, arg1, arg2);
            }
            try {
                Object[] copies = Util.copyObjects(new Object[]{arg0,arg2},_orb());
                String arg0Copy = (String) copies[0];
                Dictionary arg2Copy = (Dictionary) copies[1];
                return ((ParticipantServer)so.servant).prepare(arg0Copy, arg1, arg2Copy);
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                if (exCopy instanceof HeurHazardException) {
                    throw (HeurHazardException)exCopy;
                }
                if (exCopy instanceof HeurMixedException) {
                    throw (HeurMixedException)exCopy;
                }
                if (exCopy instanceof RollbackException) {
                    throw (RollbackException)exCopy;
                }
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
    
    public HeuristicMessage[] rollback(String arg0) throws HeurCommitException, HeurHazardException, HeurMixedException, RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("rollback", true);
                    out.write_value(arg0,String.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return (HeuristicMessage[]) in.read_value(HeuristicMessage[].class);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    if (id.equals("IDL:com/atomikos/icatch/HeurCommitEx:1.0")) {
                        throw (HeurCommitException) in.read_value(HeurCommitException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurHazardEx:1.0")) {
                        throw (HeurHazardException) in.read_value(HeurHazardException.class);
                    }
                    if (id.equals("IDL:com/atomikos/icatch/HeurMixedEx:1.0")) {
                        throw (HeurMixedException) in.read_value(HeurMixedException.class);
                    }
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return rollback(arg0);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("rollback",ParticipantServer.class);
            if (so == null) {
                return rollback(arg0);
            }
            try {
                HeuristicMessage[] result = ((ParticipantServer)so.servant).rollback(arg0);
                return (HeuristicMessage[])Util.copyObject(result,_orb());
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                if (exCopy instanceof HeurCommitException) {
                    throw (HeurCommitException)exCopy;
                }
                if (exCopy instanceof HeurHazardException) {
                    throw (HeurHazardException)exCopy;
                }
                if (exCopy instanceof HeurMixedException) {
                    throw (HeurMixedException)exCopy;
                }
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
}
