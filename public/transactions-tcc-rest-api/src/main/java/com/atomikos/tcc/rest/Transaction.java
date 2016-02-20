/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import java.util.ArrayList;
import java.util.List;



public class Transaction {

    protected List<ParticipantLink> participantLinks;
    
    public Transaction() {
	}
    
    public Transaction(List<ParticipantLink> participantLink) {
		this.participantLinks = participantLink;
	}
	
    public List<ParticipantLink> getParticipantLinks() {
        if (participantLinks == null) {
            participantLinks = new ArrayList<ParticipantLink>();
        }
        return this.participantLinks;
    }

}
