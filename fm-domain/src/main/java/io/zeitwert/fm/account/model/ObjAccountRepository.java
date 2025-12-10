
package io.zeitwert.fm.account.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjAccountRepository extends ObjRepository<ObjAccount> {

	ObjContactRepository getContactRepository();

	ObjDocumentRepository getDocumentRepository();

}
