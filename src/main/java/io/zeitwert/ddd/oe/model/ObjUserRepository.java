
package io.zeitwert.ddd.oe.model;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

public interface ObjUserRepository extends ObjRepository<ObjUser, ObjUserVRecord> {

	PasswordEncoder getPasswordEncoder();

	CodePartListType getTenantSetType();

	ObjTenantCache getTenantCache();

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(String email);

}
