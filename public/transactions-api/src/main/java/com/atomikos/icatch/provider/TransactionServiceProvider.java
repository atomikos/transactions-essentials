package com.atomikos.icatch.provider;

import java.util.Properties;

import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.admin.LogControl;

public interface TransactionServiceProvider extends TransactionService {
  
    void init(Properties properties) throws SysException;

    LogControl getLogControl();
    
    RecoveryService getRecoveryService();
    
}
