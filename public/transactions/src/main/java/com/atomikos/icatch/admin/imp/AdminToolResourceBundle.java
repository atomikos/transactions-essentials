/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

import java.util.ListResourceBundle;

/**
 *
 *
 *
 * Locale-specific values for the admintool annex LocalLogAdministrator
 */

public class AdminToolResourceBundle extends ListResourceBundle
{

    private static final Object[][] contents_ = {
            { "mainMenuName", "Main" },
            { "showActiveTransactionsMenuItemName", "Show Active Transactions" },
            { "exitMenuItemName", "Exit" },
            {
                    "shutdownMessage",
                    "Shutdown is prevented by active transactions. Would you like to force shutdown anyway?" },
            { "shutdownTitle", "Shutdown" },
            { "adminToolTitle", "Atomikos Log Inspector" },
            { "appendDialogTitle", "Append to..." },
            { "commitAttemptedMessage", "COMMIT attempted of: " },
            { "rollbackAttemptedMessage", "ROLLBACK attempted of: " },
            { "ofWhichMessage", "of which: " },
            { "forgetNoArchiveOption", "Forget, no Archive" },
            { "forgetAndArchiveOption", "Forget and Archive" },
            { "commitOption", "Commit" }, { "rollbackOption", "Rollback" },
            { "keepInLogOption", "Keep in Log" },
            { "commitOutcomeMessage", "Commit attempted of tx: " },
            { "rollbackOutcomeMessage", "Rollback attempted of tx: " },
            { "rootTransactionMessage", "Root transaction: " },
            { "selectedTransactionMessage", "Selected tx: " },
            { "noDetailsAvailableMessage", "No administration details are available in this state." },
            { "noDetailsAvailableTitle", "Sorry..." },
            { "stateDetailsTitle", "State Details" } };

    protected Object[][] getContents ()
    {
        return contents_;
    }

}
