
package io.zeitwert.fm.contact.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;

public interface ObjContactRepository extends ObjRepository<ObjContact, ObjContactVRecord> {

	static CodePartListType addressListType() {
		return CodePartListTypeEnum.getPartListType("contact.addressList");
	}

	default ObjContactPartAddressRepository getAddressRepository() {
		return this.getAppContext().getBean(ObjContactPartAddressRepository.class);
	}

}
