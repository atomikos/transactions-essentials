package com.atomikos.recovery.imp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;

public class LogControlImp implements com.atomikos.icatch.admin.LogControl {

	private final AdminLog adminLog;
	
    LogControlImp ( AdminLog adminLog )
    {
        this.adminLog = adminLog;
    }

	@Override
	public AdminTransaction[] getAdminTransactions() {
		String[] tids=adminLog.getPendingCoordinatorIds();
		AdminTransaction[] ret = new AdminTransaction[tids.length];
		for (int i = 0; i < tids.length; i++) {
			ret[i]= new AdminTransactionImp(tids[i]);
		}
		
		
		return ret;
	}

	@Override
	public AdminTransaction[] getAdminTransactions(String[] tids) {
		List<String> tidsToFind = Arrays.asList(tids);
		String[] pendingCoordinatorIds=adminLog.getPendingCoordinatorIds();
		
		Set<AdminTransaction> adminTransactions = new HashSet<AdminTransaction>();
		for (String pendingCoordinatorId : pendingCoordinatorIds) {
			if(tidsToFind.contains(pendingCoordinatorId)) {
				adminTransactions.add(new AdminTransactionImp(pendingCoordinatorId));
			}
			
		}
		return adminTransactions.toArray(new AdminTransaction[adminTransactions.size()]);
	}

}
