package com.atomikos.icatch.provider;

import java.util.Properties;

import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.admin.LogControl;

public interface TransactionServiceProvider extends TransactionService {
  
    public void init(Properties properties) throws SysException;

    public LogControl getLogControl();
    
    public RecoveryService getRecoveryService();
}
