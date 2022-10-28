
package io.zeitwert.server.session.service.api;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeLocale;
import io.zeitwert.ddd.session.model.SessionInfo;

public interface SessionService {

	static final String DEFAULT_LOCALE = "de-CH";

	static final String AUTH_HEADER_PREFIX = "Bearer ";

	/**
	 * Server session: open session for given user
	 * 
	 * @param user user
	 * @return session info
	 */
	SessionInfo openSession(ObjUser user);

	/**
	 * Server session: open session for given user
	 * 
	 * @param user   user
	 * @param locale desired locale
	 * @return session info
	 */
	SessionInfo openSession(ObjUser user, CodeLocale locale);

	/**
	 * Server session: close session
	 * 
	 * @param session session
	 */
	void closeSession(SessionInfo sessionInfo);

}
