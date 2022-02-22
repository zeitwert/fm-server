
package fm.comunas.ddd.session.service.api;

import fm.comunas.ddd.common.model.enums.CodeLocale;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.session.model.SessionInfo;

public interface SessionService {

	static final String AUTH_HEADER_PREFIX = "Bearer ";

	/**
	 * Global session for shared objects (e.g. ObjTenant, ObjUser)
	 * 
	 * @return global session
	 */
	SessionInfo getGlobalSession();

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
