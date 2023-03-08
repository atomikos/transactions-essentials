/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import javax.transaction.xa.XAException;

public class XAExceptionHelper {

	public static String convertErrorCodeToVerboseMessage(int errorCode) {
		String msg = "unkown";
		switch (errorCode) {
		case XAException.XAER_RMFAIL:
			msg = "the XA resource has become unavailable";
			break;
		case XAException.XA_RBROLLBACK:
			msg = "the XA resource has rolled back for an unspecified reason";
			break;
		case XAException.XA_RBCOMMFAIL:
			msg = "the XA resource rolled back due to a communication failure";
			break;
		case XAException.XA_RBDEADLOCK:
			msg = "the XA resource has rolled back because of a deadlock";
			break;
		case XAException.XA_RBINTEGRITY:
			msg = "the XA resource has rolled back due to a constraint violation";
			break;
		case XAException.XA_RBOTHER:
			msg = "the XA resource has rolled back for an unknown reason";
			break;
		case XAException.XA_RBPROTO:
			msg = "the XA resource has rolled back because it did not expect this command in the current context";
			break;
		case XAException.XA_RBTIMEOUT:
			msg = "the XA resource has rolled back because the transaction took too long";
			break;
		case XAException.XA_RBTRANSIENT:
			msg = "the XA resource has rolled back for a temporary reason - the transaction can be retried later";
			break;
		case XAException.XA_NOMIGRATE:
			msg = "XA resume attempted in a different place from where suspend happened";
			break;
		case XAException.XA_HEURHAZ:
			msg = "the XA resource may have heuristically completed the transaction";
			break;
		case XAException.XA_HEURCOM:
			msg = "the XA resource has heuristically committed";
			break;
		case XAException.XA_HEURRB:
			msg = "the XA resource has heuristically rolled back";
			break;
		case XAException.XA_HEURMIX:
			msg = "the XA resource has heuristically committed some parts and rolled back other parts";
			break;
		case XAException.XA_RETRY:
			msg = "the XA command had no effect and may be retried";
			break;
		case XAException.XA_RDONLY:
			msg = "the XA resource had no updates to perform for this transaction";
			break;
		case XAException.XAER_RMERR:
			msg = "the XA resource detected an internal error";
			break;
		case XAException.XAER_NOTA:
			msg = "the supplied XID is invalid for this XA resource";
			break;
		case XAException.XAER_INVAL:
			msg = "invalid arguments were given for the XA operation";
			break;
		case XAException.XAER_PROTO:
			msg = "the XA resource did not expect this command in the current context";
			break;
		case XAException.XAER_DUPID:
			msg = "the supplied XID already exists in this XA resource";
			break;
		case XAException.XAER_OUTSIDE:
			msg = "the XA resource is currently involved in a local (non-XA) transaction";
			break;
		default:
			msg = "unknown";
		}
		return msg;
	}
	
	public static String formatLogMessage(String msg, XAException e, String impact) {
		StringBuilder ret = new StringBuilder();
		ret.append(msg).
			append(": ").
			append(convertErrorCodeToVerboseMessage(e.errorCode)).
			append(" - ").
			append(impact);
		return ret.toString();
	}
}
