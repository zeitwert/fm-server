
package io.zeitwert.fm.contact.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;

public interface ObjContactRepository extends ObjRepository<ObjContact, ObjContactVRecord> {

	static CodePartListType addressListType() {
		return CodePartListTypeEnum.getPartListType("contact.addressList");
	}

	static ObjContactPartAddressRepository getAddressRepository() {
		return AppContext.getInstance().getBean(ObjContactPartAddressRepository.class);
	}

}
