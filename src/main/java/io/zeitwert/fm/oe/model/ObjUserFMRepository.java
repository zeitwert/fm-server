
package io.zeitwert.fm.oe.model;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

public interface ObjUserFMRepository
		extends ObjRepository<ObjUserFM, ObjUserVRecord> {

	static CodePartListType tenantListType() {
		return CodePartListTypeEnum.getPartListType("user.tenantList");
	}

	PasswordEncoder getPasswordEncoder();

	ObjTenantCache getTenantCache();

	/**
	 * Lookup User with email
	 */
	Optional<ObjUserFM> getByEmail(String email);

}
