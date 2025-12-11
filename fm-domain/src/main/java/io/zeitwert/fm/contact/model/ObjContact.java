
package io.zeitwert.fm.contact.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.account.model.ItemWithAccount;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface ObjContact extends Obj, ItemWithAccount, ItemWithNotes, ItemWithTasks {

	@Override
	ObjAccount getAccount();

	CodeContactRole getContactRole();

	void setContactRole(CodeContactRole contactRole);

	CodeSalutation getSalutation();

	void setSalutation(CodeSalutation salutation);

	CodeTitle getTitle();

	void setTitle(CodeTitle title);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	LocalDate getBirthDate();

	void setBirthDate(LocalDate birthDate);

	String getPhone();

	void setPhone(String phone);

	String getMobile();

	void setMobile(String mobile);

	String getEmail();

	void setEmail(String email);

	String getDescription();

	void setDescription(String description);

	List<ObjContactPartAddress> getMailAddressList();

	Optional<ObjContactPartAddress> getMailAddress(Integer addressId);

	void clearMailAddressList();

	ObjContactPartAddress addMailAddress();

	void removeMailAddress(Integer addressId);

	List<ObjContactPartAddress> getElectronicAddressList();

	Optional<ObjContactPartAddress> getElectronicAddress(Integer addressId);

	void clearElectronicAddressList();

	ObjContactPartAddress addElectronicAddress();

	void removeElectronicAddress(Integer addressId);

}
