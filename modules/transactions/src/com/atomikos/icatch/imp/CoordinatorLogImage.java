//
//  CoordinatorLogImage.java
//  icatch
//
//  Created by guy on Thu Mar 29 2001.
//  
//

package com.atomikos.icatch.imp;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Vector;

import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.TxState;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

/**
 * 
 * 
 * A log image for CoordinatorImp instances.
 */

class CoordinatorLogImage implements ObjectImage
{
    // force serial version UID for backward log compatibility
    static final long serialVersionUID = 3404629869531420208L;

    String root_;

    TxState state_;

    boolean heuristicCommit_;
    // what to do on timeout of indoubt: commit or not

    Vector participants_;
    // all participants known for this coordinator.

    RecoveryCoordinator coordinator_;
    // the recovery coordinator; null if root.

    long maxInquiries_;
    // max no of indoubt inquiries before heuristic

    CoordinatorStateHandler stateHandler_;

    boolean activity_;
    //activity or not
    
    int localSiblingCount_;
    //for activity: active state to recover
    
    boolean checkSiblings_;
    //for activity: active state to recover
    
    boolean single_threaded_2pc_;
    //2PC without threads desired?
    
    /**
     * Required by Externalizable interface.
     */

    public CoordinatorLogImage ()
    {

    }

    /**
     * Constructor for non-activities.
     * 
     * @param root
     * @param state
     * @param participants
     * @param coordinator
     * @param commit_on_heuristic
     * @param maxinquiries
     * @param stateHandler
     * @param single_threaded_2pc
     */
    public CoordinatorLogImage ( String root , TxState state ,
            Vector participants , RecoveryCoordinator coordinator ,
            boolean commit_on_heuristic , long maxinquiries ,
            CoordinatorStateHandler stateHandler , boolean single_threaded_2pc )
    {
        root_ = root;
        state_ = state;
        participants_ = participants;
        coordinator_ = coordinator;
        heuristicCommit_ = commit_on_heuristic;
        maxInquiries_ = maxinquiries;
        stateHandler_ = stateHandler;
        activity_ = false;
        localSiblingCount_ = 0;
        checkSiblings_ = false;
        single_threaded_2pc_ = single_threaded_2pc;
    }
    
    /**
     * Constructor for activities in active state.
     * 
     * @param root
     * @param state
     * @param participants
     * @param coordinator
     * @param commit_on_heuristic
     * @param maxinquiries
     * @param stateHandler
     * @param localSiblingCount
     * @param checkSiblings
     * @param single_threaded_2pc
     */
    public CoordinatorLogImage ( String root , TxState state ,
            Vector participants , RecoveryCoordinator coordinator ,
            boolean commit_on_heuristic , long maxinquiries ,
            CoordinatorStateHandler stateHandler , int localSiblingCount ,
            boolean checkSiblings , boolean single_threaded_2pc
            
            )
    {
        this ( root , state , participants , coordinator , commit_on_heuristic , 
                maxinquiries , stateHandler , single_threaded_2pc );
        activity_ = true;
        localSiblingCount_ = localSiblingCount;
        checkSiblings_ = checkSiblings;
    }

    public Object getId ()
    {
        return root_;
    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        // make sure to CLONE all objects, to avoid that
        // subsequent calls do NOT re-write the objects
        // (SERIALIZATION!)
        out.writeObject ( root_ );
        out.writeObject ( state_ );
        out.writeObject ( participants_.clone () );
        if ( coordinator_ == null )
            out.writeBoolean ( false );
        else {
            out.writeBoolean ( true );
            out.writeObject ( coordinator_ );
        }
        out.writeBoolean ( heuristicCommit_ );
        out.writeLong ( maxInquiries_ );
        out.writeObject ( stateHandler_.clone () );
        out.writeBoolean ( activity_ );
        if ( activity_ ) {
            out.writeInt ( localSiblingCount_ );
            out.writeBoolean ( checkSiblings_ );
        }
        out.writeBoolean ( single_threaded_2pc_ );

    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        try {
			root_ = (String) in.readObject ();
			state_ = (TxState) in.readObject ();
			participants_ = (Vector) in.readObject ();
			boolean readcoord = in.readBoolean ();
			if ( readcoord )
			    coordinator_ = (RecoveryCoordinator) in.readObject ();
			heuristicCommit_ = in.readBoolean ();
			maxInquiries_ = in.readLong ();
			stateHandler_ = (CoordinatorStateHandler) in.readObject ();
			activity_ = in.readBoolean();
			if ( activity_ ) {
			    localSiblingCount_ = in.readInt();
			    checkSiblings_ = in.readBoolean();
			}
			single_threaded_2pc_ = in.readBoolean();
		} catch (InvalidClassException ex) {
			// fix for 22174
			throw (IOException) new IOException ( "Object of class " + ex.classname + " in transaction log" +
					" not compatible with version found in classpath" ).initCause(ex);
		}
    }

    public Recoverable restore ()
    {
        CoordinatorImp coord = new CoordinatorImp ();

        coord.restore ( this );

        return coord;
    }

}
