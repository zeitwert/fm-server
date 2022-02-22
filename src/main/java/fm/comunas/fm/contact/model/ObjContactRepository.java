
package fm.comunas.fm.contact.model;

import java.util.List;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.contact.model.db.tables.records.ObjContactVRecord;

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
