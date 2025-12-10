package io.zeitwert.fm.oe.model;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjUserFMRepository extends ObjRepository<ObjUserFM> {

	PasswordEncoder getPasswordEncoder();

	ObjDocumentRepository getDocumentRepository();

	Optional<ObjUserFM> getByEmail(String email);

	boolean isAppAdmin(ObjUserFM user);

	boolean isAdmin(ObjUserFM user);

}
