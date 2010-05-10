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
