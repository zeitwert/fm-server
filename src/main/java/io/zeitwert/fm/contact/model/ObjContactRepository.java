
package io.zeitwert.fm.contact.model;

import java.util.List;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;

public interface ObjContactRepository extends ObjRepository<ObjContact, ObjContactVRecord> {

	ObjContactPartAddressRepository getAddressRepository();

	CodePartListType getAddressListType();

	/**
	 * Change the owner to a list of contacts.
	 *
	 * @param contacts
	 */
	void changeOwner(List<ObjContact> contacts, ObjUser user);

}
