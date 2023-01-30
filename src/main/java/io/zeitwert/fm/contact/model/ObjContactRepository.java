
package io.zeitwert.fm.contact.model;

import java.util.List;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjContactRepository extends FMObjRepository<ObjContact, ObjContactVRecord> {

	ObjContactPartAddressRepository getAddressRepository();

	CodePartListType getAddressListType();

	/**
	 * Change the owner to a list of contacts.
	 *
	 * @param contacts
	 */
	void changeOwner(List<ObjContact> contacts, ObjUser user);

}
