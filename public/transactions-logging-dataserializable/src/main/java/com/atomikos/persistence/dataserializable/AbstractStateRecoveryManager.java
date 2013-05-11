package com.atomikos.persistence.dataserializable;

import java.util.Enumeration;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.icatch.TxState;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.StateObjectImage;

public abstract class AbstractStateRecoveryManager implements StateRecoveryManager, FSMPreEnterListener<TxState> {

	protected ObjectLog objectlog_;


	/**
	 * @see StateRecoveryManager
	 */
	public void register(StateRecoverable<TxState> staterecoverable) {
		if (staterecoverable == null)
			throw new IllegalArgumentException("null in register arg");
		TxState[] states = staterecoverable.getRecoverableStates();
		if (states != null) {
			for (int i = 0; i < states.length; i++) {
				staterecoverable.addFSMPreEnterListener(this, states[i]);
			}
			states = staterecoverable.getFinalStates();
			for (int i = 0; i < states.length; i++) {
				staterecoverable.addFSMPreEnterListener(this, states[i]);
			}
		}
	}

	/**
	 * @see FSMPreEnterListener
	 */
	public void preEnter(FSMEnterEvent<TxState> event) throws IllegalStateException {
		TxState state = event.getState();
		StateRecoverable<TxState> source = (StateRecoverable<TxState>) event.getSource();
		ObjectImage img = source.getObjectImage(state);
		if (img != null) {
			// null images are not logged as per the Recoverable contract
			StateObjectImage simg = new StateObjectImage(img);
			Object[] finalstates = source.getFinalStates();
			boolean delete = false;

			for (int i = 0; i < finalstates.length; i++) {
				if (state.equals(finalstates[i]))
					delete = true;
			}

			try {
				if (!delete)
					objectlog_.flush(simg);
				else
					objectlog_.delete(simg.getId());
			} catch (LogException le) {
				le.printStackTrace();
				throw new IllegalStateException("could not flush state image " + le.getMessage() + " " + le.getClass().getName());
			}
		}

	}

	/**
	 * @see StateRecoveryManager
	 */
	public void close() throws LogException {
		objectlog_.close();
	}

	/**
	 * @see StateRecoveryManager
	 */
	public StateRecoverable<TxState> recover(Object id) throws LogException {
		StateRecoverable<TxState> srec = (StateRecoverable<TxState>) objectlog_.recover(id);
		if (srec != null) // null if not found!
			register(srec);
		return srec;
	}

	/**
	 * @see StateRecoveryManager
	 */
	public Vector<StateRecoverable<TxState>> recover() throws LogException {
		Vector<StateRecoverable<TxState>> ret = objectlog_.recover();
		Enumeration<StateRecoverable<TxState>> enumm = ret.elements();
		while (enumm.hasMoreElements()) {
			StateRecoverable<TxState> srec = (StateRecoverable<TxState>) enumm.nextElement();
			register(srec);
		}
		return ret;
	}

	/**
	 * @see StateRecoveryManager
	 */
	public void delete(Object id) throws LogException {
		objectlog_.delete(id);
	}

}