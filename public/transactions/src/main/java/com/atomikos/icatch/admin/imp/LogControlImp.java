package com.atomikos.icatch.admin.imp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;

public class LogControlImp implements com.atomikos.icatch.admin.LogControl {

	private final AdminLog adminLog;
	
	public LogControlImp ( AdminLog adminLog )
    {
        this.adminLog = adminLog;
    }

	@Override
	public AdminTransaction[] getAdminTransactions() {
		CoordinatorLogEntry[] tids=adminLog.getCoordinatorLogEntries();
		AdminTransaction[] ret = new AdminTransaction[tids.length];
		for (int i = 0; i < tids.length; i++) {
			ret[i] = convertToAdminTransaction(tids[i]);
		}
		
		
		return ret;
	}

	private AdminTransactionImp convertToAdminTransaction(
			CoordinatorLogEntry coordinatorLogEntry) {
		return new AdminTransactionImp(coordinatorLogEntry, adminLog);
	}

	@Override
	public AdminTransaction[] getAdminTransactions(String... tids) {
		List<String> tidsToFind = Arrays.asList(tids);
		CoordinatorLogEntry[] pendingCoordinatorEntries=adminLog.getCoordinatorLogEntries();
		
		Set<AdminTransaction> adminTransactions = new HashSet<AdminTransaction>();
		for (CoordinatorLogEntry pendingCoordinatorEntry : pendingCoordinatorEntries) {
			if(tidsToFind.contains(pendingCoordinatorEntry.coordinatorId)) {
				adminTransactions.add(convertToAdminTransaction(pendingCoordinatorEntry));
			}
			
		}
		return adminTransactions.toArray(new AdminTransaction[adminTransactions.size()]);
	}

}
