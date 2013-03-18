//$Id: TestStateRecoverable.java,v 1.4 2006/09/19 08:03:53 guy Exp $
//Revision 1.2  2006/09/12 06:20:00  guy
//Added tests for issue 10041
//
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:24:49  guy
//Updated javadoc.
//
//Revision 1.3  2002/02/20 10:11:00  guy
//Added generic test files for state recovery mech.
//
//Revision 1.2  2002/02/18 13:32:33  guy
//Added test files to package under CVS.
//
//Revision 1.1  2002/01/29 12:34:24  guy
//Added test files to package dir.
//

package com.atomikos.persistence.imp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.util.EventListener;

import com.atomikos.finitestates.FSM;
import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMImp;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.finitestates.StateMutable;
import com.atomikos.icatch.TxState;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.StateRecoverable;

/**
 * 
 * 
 * A simple implementation of a state recoverable for test purposes.
 */

public class TestStateRecoverable implements StateRecoverable<TxState>, StateMutable<TxState>, FSMPreEnterListener<TxState> {

	protected Object id_;

	protected FSM<TxState> fsm_;

	protected Hashtable<TxState, Vector<EventListener>> listeners_;

	protected boolean returnNullObjectImage_;

	// if true: return null for object image
	// to test for bug 10041

	/**
	 * Create a new instance.
	 * 
	 * @param id
	 *            The object id to use.
	 */

	public TestStateRecoverable(Object id) {
		this(id, TestTransitionTable.INITIAL);

	}

	/**
	 * Create a new instance.
	 * 
	 * @param id
	 *            The object id to use.
	 * @param state
	 *            The initial state to use.
	 */

	public TestStateRecoverable(Object id, TxState state) {
		id_ = id;
		listeners_ = new Hashtable<TxState, Vector<EventListener>>();

		fsm_ = new FSMImp<TxState>(new TestTransitionTable(), state);
	}

	/**
	 * Sets the instance to return null for getObjectImage calls. Needed for testing issue 10041
	 * 
	 */
	public void setReturnNullObjectImage() {
		returnNullObjectImage_ = true;
	}

	/**
	 * Get the id of this object.
	 * 
	 * @return Object The generic id.
	 */

	public Object getId() {
		return id_;
	}

	/**
	 * @see StateRecoverable
	 */

	public TxState[] getRecoverableStates() {
		TxState[] ret = { TestTransitionTable.MIDDLE, TestTransitionTable.END };
		return ret;
	}

	/**
	 * @see StateRecoverable
	 */

	public ObjectImage getObjectImage() {
		return getObjectImage(fsm_.getState());
	}

	/**
	 * @see StateRecoverable
	 */

	public TxState[] getFinalStates() {
		TxState[] ret = { TestTransitionTable.END };
		return ret;
	}

	/**
	 * @see StateRecoverable
	 */

	public ObjectImage getObjectImage(TxState state) {
		if (returnNullObjectImage_)
			return null;
		else
			return new TestStateRecoverableObjectImage(state, id_);
	}

	/**
	 * @see StateRecoverable
	 */

	public void addFSMPreEnterListener(FSMPreEnterListener<TxState> lstnr, TxState state) {
		fsm_.addFSMPreEnterListener(this, state);
		Vector<EventListener> listeners = (Vector<EventListener>) listeners_.get(state);
		if (listeners == null)
			listeners = new Vector<EventListener>();
		if (!listeners.contains(lstnr))
			listeners.addElement(lstnr);
		listeners_.put(state, listeners);
	}

	/**
	 * @see FSMPreEnterListener
	 */

	public void preEnter(FSMEnterEvent<TxState> e) throws IllegalStateException {
		TxState state = e.getState();
		FSMEnterEvent<TxState> event = new FSMEnterEvent<TxState>(this, state);

		Vector<EventListener> listeners = (Vector<EventListener>) listeners_.get(state);
		if (listeners == null)
			return;

		Enumeration<EventListener> enumm = listeners.elements();
		while (enumm.hasMoreElements()) {
			FSMPreEnterListener<TxState> l = (FSMPreEnterListener<TxState>) enumm.nextElement();
			l.preEnter(event);
		}
	}

	/**
	 * @see StateMutable
	 */

	public void setState(TxState state) throws IllegalStateException {
		fsm_.setState(state);
	}

	/**
	 * @see StateRecoverable
	 */

	public TxState getState() {
		return fsm_.getState();
	}

	/**
	 * @see Object
	 */

	public boolean equals(Object o) {
		if (o == null || !(o instanceof TestStateRecoverable))
			return false;

		TestStateRecoverable ts = (TestStateRecoverable) o;
		return ts.id_.equals(id_);
	}
}
