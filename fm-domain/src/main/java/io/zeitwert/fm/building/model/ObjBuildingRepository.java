
package io.zeitwert.fm.building.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface ObjBuildingRepository extends ObjRepository<ObjBuilding> {

	ObjAccountRepository getAccountRepository();

	ObjContactRepository getContactRepository();

	ObjDocumentRepository getDocumentRepository();

	DocTaskRepository getTaskRepository();

}
