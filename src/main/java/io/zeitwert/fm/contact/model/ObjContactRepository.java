
package io.zeitwert.fm.contact.model;

import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjContactRepository extends FMObjRepository<ObjContact, ObjContactVRecord> {

	static CodePartListType addressListType() {
		return CodePartListTypeEnum.getPartListType("contact.addressList");
	}

	ObjContactPartAddressRepository getAddressRepository();

}
