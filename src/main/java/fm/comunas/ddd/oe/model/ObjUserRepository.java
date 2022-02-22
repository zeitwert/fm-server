
package fm.comunas.ddd.oe.model;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.oe.model.db.tables.records.ObjUserVRecord;

import java.util.Optional;

public interface ObjUserRepository extends ObjRepository<ObjUser, ObjUserVRecord> {

	/**
	 * Lookup user with given id (in global session)
	 */
	Optional<ObjUser> get(Integer id);

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(String email);

}
