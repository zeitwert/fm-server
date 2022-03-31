
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;

import java.util.Optional;

public interface ObjUserRepository extends ObjRepository<ObjUser, ObjUserVRecord> {

	/**
	 * Lookup user with given id (in global session)
	 */
	ObjUser get(Integer id);

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(String email);

}
