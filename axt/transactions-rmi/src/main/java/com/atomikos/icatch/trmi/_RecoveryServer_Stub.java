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


public class _RecoveryServer_Stub extends Stub implements RecoveryServer {
    
    private static final String[] _type_ids = {
        "RMI:com.atomikos.icatch.trmi.RecoveryServer:0000000000000000"
    };
    
    public String[] _ids() { 
        return _type_ids;
    }
    
    public Boolean replayCompletion(String arg0, Participant arg1) throws RemoteException {
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    org.omg.CORBA_2_3.portable.OutputStream out = 
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        _request("replayCompletion", true);
                    out.write_value(arg0,String.class);
                    out.write_value((Serializable)arg1,Participant.class);
                    in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                    return (Boolean) in.read_value(Boolean.class);
                } catch (ApplicationException ex) {
                    in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                    String id = in.read_string();
                    throw new UnexpectedException(id);
                } catch (RemarshalException ex) {
                    return replayCompletion(arg0,arg1);
                } finally {
                    _releaseReply(in);
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex);
            }
        } else {
            ServantObject so = _servant_preinvoke("replayCompletion",RecoveryServer.class);
            if (so == null) {
                return replayCompletion(arg0, arg1);
            }
            try {
                Object[] copies = Util.copyObjects(new Object[]{arg0,arg1},_orb());
                String arg0Copy = (String) copies[0];
                Participant arg1Copy = (Participant) copies[1];
                Boolean result = ((RecoveryServer)so.servant).replayCompletion(arg0Copy, arg1Copy);
                return (Boolean)Util.copyObject(result,_orb());
            } catch (Throwable ex) {
                Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                throw Util.wrapException(exCopy);
            } finally {
                _servant_postinvoke(so);
            }
        }
    }
}
