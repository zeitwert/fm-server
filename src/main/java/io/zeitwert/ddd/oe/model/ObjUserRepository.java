
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.oe.service.api.TenantService;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.Optional;

public interface ObjUserRepository extends ObjRepository<ObjUser, ObjUserVRecord> {

	CodePartListType getTenantSetType();

	TenantService getTenantService();

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(String email);

}
