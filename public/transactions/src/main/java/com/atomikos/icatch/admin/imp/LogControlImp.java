/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.admin.imp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;

public class LogControlImp implements com.atomikos.icatch.admin.LogControl {

	private final AdminLog adminLog;
	
	public LogControlImp ( AdminLog adminLog )
    {
        this.adminLog = adminLog;
    }

	private AdminTransaction[] getAdminTransactions() {
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
		if (tids.length == 0) {
			return getAdminTransactions();
		}
		List<String> tidsToFind = Arrays.asList(tids);
		CoordinatorLogEntry[] pendingCoordinatorEntries=adminLog.getCoordinatorLogEntries();
		
		Set<AdminTransaction> adminTransactions = new HashSet<AdminTransaction>();
		for (CoordinatorLogEntry pendingCoordinatorEntry : pendingCoordinatorEntries) {
			if(tidsToFind.contains(pendingCoordinatorEntry.id)) {
				adminTransactions.add(convertToAdminTransaction(pendingCoordinatorEntry));
			}
			
		}
		return adminTransactions.toArray(new AdminTransaction[adminTransactions.size()]);
	}

}
