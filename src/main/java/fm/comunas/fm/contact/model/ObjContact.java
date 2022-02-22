
package fm.comunas.fm.contact.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.contact.model.enums.CodeContactRole;
import fm.comunas.fm.contact.model.enums.CodeSalutation;
import fm.comunas.fm.contact.model.enums.CodeTitle;
import fm.comunas.fm.obj.model.FMObj;

public interface ObjContact extends FMObj {

	Integer getAccountId();

	void setAccountId(Integer id);

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
