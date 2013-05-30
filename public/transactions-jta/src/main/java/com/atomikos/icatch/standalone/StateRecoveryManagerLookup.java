package com.atomikos.icatch.standalone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.OrderedUtils;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;
import com.atomikos.util.JDKServiceLoader;

public class StateRecoveryManagerLookup {

	private static final Logger LOGGER = LoggerFactory.createLogger(StateRecoveryManagerLookup.class);

	public static StateRecoveryManager lookup() {

		Iterator<StateRecoveryManager> iterator = JDKServiceLoader.lookupProviders(StateRecoveryManager.class, StateRecoveryManagerLookup.class.getClassLoader());
		if (!iterator.hasNext()) {
			LOGGER.logDebug("No  StateRecoveryManager found falling back to VolatileStateRecoveryManager");
			return new VolatileStateRecoveryManager();
		}
		// else
		List<StateRecoveryManager> candidates= new ArrayList<StateRecoveryManager>();
		
		while (iterator.hasNext()) {
			candidates.add(iterator.next());
		}
		
		StateRecoveryManager resolved = OrderedUtils.firstByOrder(candidates);
		
		if(LOGGER.isDebugEnabled()){
			LOGGER.logDebug("Selected StateRecoveryManager impl "+resolved.getClass());	
		}
		
		return resolved;
	}
	
	
}
