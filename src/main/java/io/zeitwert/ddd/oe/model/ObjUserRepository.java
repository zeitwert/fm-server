
package io.zeitwert.ddd.oe.model;

import java.util.Optional;

import org.jooq.TableRecord;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;

public interface ObjUserRepository extends ObjRepository<ObjUser, TableRecord<?>> {

	PasswordEncoder getPasswordEncoder();

	ObjTenantCache getTenantCache();

	/**
	 * Lookup User with email
	 */
	Optional<ObjUser> getByEmail(String email);

}
