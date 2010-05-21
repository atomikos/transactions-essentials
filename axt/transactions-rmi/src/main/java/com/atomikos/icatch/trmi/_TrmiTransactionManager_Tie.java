package com.atomikos.icatch.trmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Dictionary;

import javax.rmi.CORBA.Tie;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.ObjectImpl;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;


public class _TrmiTransactionManager_Tie extends ObjectImpl implements Tie {
    
    private TrmiTransactionManager target = null;
    
    private static final String[] _type_ids = {
        "RMI:com.atomikos.icatch.trmi.TrmiTransactionManager:61C3B0F66DD2DFFC:2DFC8E467C50A4FD", 
        "RMI:com.atomikos.icatch.trmi.CompositeTransactionServer:0000000000000000", 
        "RMI:com.atomikos.icatch.trmi.ParticipantServer:0000000000000000", 
        "RMI:com.atomikos.icatch.trmi.RecoveryServer:0000000000000000"
    };
    
    public void setTarget(Remote target) {
        this.target = (TrmiTransactionManager) target;
    }
    
    public Remote getTarget() {
        return target;
    }
    
    public org.omg.CORBA.Object thisObject() {
        return this;
    }
    
    public void deactivate() {
        _orb().disconnect(this);
        _set_delegate(null);
        target = null;
    }
    
    public ORB orb() {
        return _orb();
    }
    
    public void orb(ORB orb) {
        orb.connect(this);
    }
    
    public String[] _ids() { 
        return _type_ids;
    }
    
    public OutputStream  _invoke(String method, InputStream _in, ResponseHandler reply) throws SystemException {
        try {
            org.omg.CORBA_2_3.portable.InputStream in = 
                (org.omg.CORBA_2_3.portable.InputStream) _in;
            switch (method.length()) {
                case 6: 
                    if (method.equals("commit")) {
                        String arg0 = (String) in.read_value(String.class);
                        HeuristicMessage[] result;
                        try {
                            result = target.commit(arg0);
                        } catch (HeurRollbackException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurRollbackEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurRollbackException.class);
                            return out;
                        } catch (HeurHazardException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurHazardEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurHazardException.class);
                            return out;
                        } catch (HeurMixedException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurMixedEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurMixedException.class);
                            return out;
                        } catch (RollbackException ex) {
                            String id = "IDL:com/atomikos/icatch/RollbackEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,RollbackException.class);
                            return out;
                        }
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(cast_array(result),HeuristicMessage[].class);
                        return out;
                    } else if (method.equals("forget")) {
                        String arg0 = (String) in.read_value(String.class);
                        target.forget(arg0);
                        OutputStream out = reply.createReply();
                        return out;
                    }
                case 7: 
                    if (method.equals("prepare")) {
                        String arg0 = (String) in.read_value(String.class);
                        int arg1 = in.read_long();
                        Dictionary arg2 = (Dictionary) in.read_value(Dictionary.class);
                        int result;
                        try {
                            result = target.prepare(arg0, arg1, arg2);
                        } catch (RollbackException ex) {
                            String id = "IDL:com/atomikos/icatch/RollbackEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,RollbackException.class);
                            return out;
                        } catch (HeurHazardException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurHazardEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurHazardException.class);
                            return out;
                        } catch (HeurMixedException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurMixedEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurMixedException.class);
                            return out;
                        }
                        OutputStream out = reply.createReply();
                        out.write_long(result);
                        return out;
                    }
                case 8: 
                    if (method.equals("rollback")) {
                        String arg0 = (String) in.read_value(String.class);
                        HeuristicMessage[] result;
                        try {
                            result = target.rollback(arg0);
                        } catch (HeurCommitException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurCommitEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurCommitException.class);
                            return out;
                        } catch (HeurMixedException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurMixedEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurMixedException.class);
                            return out;
                        } catch (HeurHazardException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurHazardEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurHazardException.class);
                            return out;
                        }
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(cast_array(result),HeuristicMessage[].class);
                        return out;
                    }
                case 14: 
                    if (method.equals("commitOnePhase")) {
                        String arg0 = (String) in.read_value(String.class);
                        int arg1 = in.read_long();
                        Dictionary arg2 = (Dictionary) in.read_value(Dictionary.class);
                        HeuristicMessage[] result;
                        try {
                            result = target.commitOnePhase(arg0, arg1, arg2);
                        } catch (HeurRollbackException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurRollbackEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurRollbackException.class);
                            return out;
                        } catch (HeurHazardException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurHazardEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurHazardException.class);
                            return out;
                        } catch (HeurMixedException ex) {
                            String id = "IDL:com/atomikos/icatch/HeurMixedEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,HeurMixedException.class);
                            return out;
                        } catch (RollbackException ex) {
                            String id = "IDL:com/atomikos/icatch/RollbackEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,RollbackException.class);
                            return out;
                        }
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(cast_array(result),HeuristicMessage[].class);
                        return out;
                    } else if (method.equals("addParticipant")) {
                        Participant arg0 = (Participant) in.read_value(Participant.class);
                        String arg1 = (String) in.read_value(String.class);
                        RecoveryCoordinatorProxy result = target.addParticipant(arg0, arg1);
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(result,RecoveryCoordinatorProxy.class);
                        return out;
                    }
                case 16: 
                    if (method.equals("replayCompletion")) {
                        String arg0 = (String) in.read_value(String.class);
                        Participant arg1 = (Participant) in.read_value(Participant.class);
                        Boolean result = target.replayCompletion(arg0, arg1);
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(result,Boolean.class);
                        return out;
                    }
                case 24: 
                    if (method.equals("addSubTxAwareParticipant")) {
                        SubTxAwareParticipant arg0 = (SubTxAwareParticipant) in.read_value(SubTxAwareParticipant.class);
                        String arg1 = (String) in.read_value(String.class);
                        target.addSubTxAwareParticipant(arg0, arg1);
                        OutputStream out = reply.createReply();
                        return out;
                    }
            }
            throw new BAD_OPERATION();
        } catch (SystemException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnknownException(ex);
        }
    }
    
    // This method is required as a work-around for
    // a bug in the JDK 1.1.6 verifier.
    
    private Serializable cast_array(Object obj) {
        return (Serializable)obj;
    }
}
