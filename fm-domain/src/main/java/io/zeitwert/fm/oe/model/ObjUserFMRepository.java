package io.zeitwert.fm.oe.model;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import io.dddrive.core.obj.model.ObjRepository;
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjUserFMRepository extends ObjRepository<ObjUserFM> {

	PasswordEncoder getPasswordEncoder();

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// ObjDocumentRepository getDocumentRepository();

	/**
	 * Lookup User with email
	 */
	Optional<ObjUserFM> getByEmail(String email);

	boolean isAppAdmin(ObjUserFM user);

	boolean isAdmin(ObjUserFM user);

}
