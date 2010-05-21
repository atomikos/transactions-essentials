package com.atomikos.icatch.trmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SubTxAwareParticipant;


public class _CompositeTransactionServer_Stub extends Stub implements CompositeTransactionServer {
    
    private static final String[] _type_ids = {
        "RMI:com.atomikos.icatch.trmi.CompositeTransactionServer:0000000000000000"
    };
    
    public String[] _ids() { 
        return _type_ids;
    }
    
    public RecoveryCoordinatorProxy addParticipant(Participant arg0, String arg1) throws RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("addParticipant", true);
                    out.write_value((Serializable)arg0,Participant.class);
                    out.write_value(arg1,String.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return (RecoveryCoordinatorProxy) in.read_value(RecoveryCoordinatorProxy.class);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return addParticipant(arg0,arg1);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("addParticipant",CompositeTransactionServer.class);
            if (so == null) {
                return addParticipant(arg0, arg1);
            }
            try {
                Object[] copies = Util.copyObjects(new Object[]{arg0,arg1},_orb());
                Participant arg0Copy = (Participant) copies[0];
                String arg1Copy = (String) copies[1];
                RecoveryCoordinatorProxy result = ((CompositeTransactionServer)so.servant).addParticipant(arg0Copy, arg1Copy);
                return (RecoveryCoordinatorProxy)Util.copyObject(result,_orb());
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
    
    public void addSubTxAwareParticipant(SubTxAwareParticipant arg0, String arg1) throws RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("addSubTxAwareParticipant", true);
                    out.write_value((Serializable)arg0,SubTxAwareParticipant.class);
                    out.write_value(arg1,String.class);
                    _invoke(out);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    addSubTxAwareParticipant(arg0,arg1);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("addSubTxAwareParticipant",CompositeTransactionServer.class);
            if (so == null) {
                addSubTxAwareParticipant(arg0, arg1);
                return ;
            }
            try {
                Object[] copies = Util.copyObjects(new Object[]{arg0,arg1},_orb());
                SubTxAwareParticipant arg0Copy = (SubTxAwareParticipant) copies[0];
                String arg1Copy = (String) copies[1];
                ((CompositeTransactionServer)so.servant).addSubTxAwareParticipant(arg0Copy, arg1Copy);
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
}
