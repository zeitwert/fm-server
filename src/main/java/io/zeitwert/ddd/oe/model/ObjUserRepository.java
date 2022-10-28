
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.session.model.RequestContext;

import java.util.Optional;

public interface ObjUserRepository extends ObjRepository<ObjUser, ObjUserVRecord> {

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(RequestContext requestCtx, String email);

}
