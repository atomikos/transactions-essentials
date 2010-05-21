package com.atomikos.icatch.trmi;

public final class TrmiTransactionManager_Skel
    implements java.rmi.server.Skeleton
{
    private static final java.rmi.server.Operation[] operations = {
	new java.rmi.server.Operation("com.atomikos.icatch.trmi.RecoveryCoordinatorProxy addParticipant(com.atomikos.icatch.Participant, java.lang.String)"),
	new java.rmi.server.Operation("void addSubTxAwareParticipant(com.atomikos.icatch.SubTxAwareParticipant, java.lang.String)"),
	new java.rmi.server.Operation("com.atomikos.icatch.HeuristicMessage commit(java.lang.String)[]"),
	new java.rmi.server.Operation("com.atomikos.icatch.HeuristicMessage commitOnePhase(java.lang.String, int, java.util.Dictionary)[]"),
	new java.rmi.server.Operation("void forget(java.lang.String)"),
	new java.rmi.server.Operation("int prepare(java.lang.String, int, java.util.Dictionary)"),
	new java.rmi.server.Operation("java.lang.Boolean replayCompletion(java.lang.String, com.atomikos.icatch.Participant)"),
	new java.rmi.server.Operation("com.atomikos.icatch.HeuristicMessage rollback(java.lang.String)[]")
    };
    
    private static final long interfaceHash = 9008997189527120110L;
    
    public java.rmi.server.Operation[] getOperations() {
	return (java.rmi.server.Operation[]) operations.clone();
    }
    
    public void dispatch(java.rmi.Remote obj, java.rmi.server.RemoteCall call, int opnum, long hash)
	throws java.lang.Exception
    {
	if (opnum < 0) {
	    if (hash == -7498011041286730506L) {
		opnum = 0;
	    } else if (hash == -8438551629400399220L) {
		opnum = 1;
	    } else if (hash == -6785758951217652594L) {
		opnum = 2;
	    } else if (hash == -3829391615912805741L) {
		opnum = 3;
	    } else if (hash == -6793985004451225327L) {
		opnum = 4;
	    } else if (hash == 5010699337017937512L) {
		opnum = 5;
	    } else if (hash == 2313171612230415664L) {
		opnum = 6;
	    } else if (hash == -3797690192234522552L) {
		opnum = 7;
	    } else {
		throw new java.rmi.UnmarshalException("invalid method hash");
	    }
	} else {
	    if (hash != interfaceHash)
		throw new java.rmi.server.SkeletonMismatchException("interface hash mismatch");
	}
	
	com.atomikos.icatch.trmi.TrmiTransactionManager server = (com.atomikos.icatch.trmi.TrmiTransactionManager) obj;
	switch (opnum) {
	case 0: // addParticipant(Participant, String)
	{
	    com.atomikos.icatch.Participant $param_Participant_1;
	    java.lang.String $param_String_2;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_Participant_1 = (com.atomikos.icatch.Participant) in.readObject();
		$param_String_2 = (java.lang.String) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    com.atomikos.icatch.trmi.RecoveryCoordinatorProxy $result = server.addParticipant($param_Participant_1, $param_String_2);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 1: // addSubTxAwareParticipant(SubTxAwareParticipant, String)
	{
	    com.atomikos.icatch.SubTxAwareParticipant $param_SubTxAwareParticipant_1;
	    java.lang.String $param_String_2;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_SubTxAwareParticipant_1 = (com.atomikos.icatch.SubTxAwareParticipant) in.readObject();
		$param_String_2 = (java.lang.String) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    server.addSubTxAwareParticipant($param_SubTxAwareParticipant_1, $param_String_2);
	    try {
		call.getResultStream(true);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 2: // commit(String)
	{
	    java.lang.String $param_String_1;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    com.atomikos.icatch.HeuristicMessage[] $result = server.commit($param_String_1);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 3: // commitOnePhase(String, int, Dictionary)
	{
	    java.lang.String $param_String_1;
	    int $param_int_2;
	    java.util.Dictionary $param_Dictionary_3;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
		$param_int_2 = in.readInt();
		$param_Dictionary_3 = (java.util.Dictionary) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    com.atomikos.icatch.HeuristicMessage[] $result = server.commitOnePhase($param_String_1, $param_int_2, $param_Dictionary_3);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 4: // forget(String)
	{
	    java.lang.String $param_String_1;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    server.forget($param_String_1);
	    try {
		call.getResultStream(true);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 5: // prepare(String, int, Dictionary)
	{
	    java.lang.String $param_String_1;
	    int $param_int_2;
	    java.util.Dictionary $param_Dictionary_3;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
		$param_int_2 = in.readInt();
		$param_Dictionary_3 = (java.util.Dictionary) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    int $result = server.prepare($param_String_1, $param_int_2, $param_Dictionary_3);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeInt($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 6: // replayCompletion(String, Participant)
	{
	    java.lang.String $param_String_1;
	    com.atomikos.icatch.Participant $param_Participant_2;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
		$param_Participant_2 = (com.atomikos.icatch.Participant) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    java.lang.Boolean $result = server.replayCompletion($param_String_1, $param_Participant_2);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 7: // rollback(String)
	{
	    java.lang.String $param_String_1;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_String_1 = (java.lang.String) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    com.atomikos.icatch.HeuristicMessage[] $result = server.rollback($param_String_1);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	default:
	    throw new java.rmi.UnmarshalException("invalid method number");
	}
    }
}
