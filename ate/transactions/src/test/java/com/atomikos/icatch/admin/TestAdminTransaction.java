package com.atomikos.icatch.admin;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.SysException;

public class TestAdminTransaction implements AdminTransaction {

	private String tid;
	private boolean wasCommitted;
	private HeuristicMessage[] tags;
	private HeuristicMessage[] msgs;
	private int state;
	private boolean forceForgetCalled;
	private boolean forceCommitCalled;
	private boolean forceRollbackCalled;
	
	public TestAdminTransaction ( String tid )
	{
		this.tid = tid;
	}
	
	public void setState (int state )
	{
		this.state = state;
	}
	
	public void setWasCommitted ( boolean value )
	{
		this.wasCommitted = value;
	}
	
	public void setTags ( HeuristicMessage[] tags )
	{
		this.tags = tags;
	}
	
	public void setHeuristicMessages ( HeuristicMessage[] msgs )
	{
		this.msgs = msgs;
	}
	
	public String getTid() {
		return tid;
	}

	public int getState() {
		return state;
	}

	public HeuristicMessage[] getTags() {
		return tags;
	}

	public HeuristicMessage[] getHeuristicMessages() {
		return msgs;
	}

	public HeuristicMessage[] getHeuristicMessages(int state) {
		
		return null;
	}

	public boolean wasCommitted() {
		return wasCommitted;
	}

	public void forceCommit() throws HeurRollbackException,
			HeurHazardException, HeurMixedException, SysException {
		forceCommitCalled = true;
	}

	public void forceRollback() throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException {
		forceRollbackCalled = true;

	}

	public void forceForget() {
		forceForgetCalled = true;

	}

	public boolean isForceCommitCalled() {
		return forceCommitCalled;
	}

	public boolean isForceForgetCalled() {
		return forceForgetCalled;
	}

	public boolean isForceRollbackCalled() {
		return forceRollbackCalled;
	}

}
