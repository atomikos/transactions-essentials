package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A message indicating that the sending participant needs to be informed about
 * the outcome. The receiver should reply with a StateMessage, an ErrorMessage
 * or with the last CommitMessage or RollbackMessage sent.
 */

public interface ReplayMessage extends TransactionMessage
{

}
