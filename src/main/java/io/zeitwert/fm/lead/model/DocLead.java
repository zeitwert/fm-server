
package io.zeitwert.fm.lead.model;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.enums.CodeArea;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.lead.model.enums.CodeLeadRating;
import io.zeitwert.fm.lead.model.enums.CodeLeadSource;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.obj.model.Obj;

import java.util.Set;

public interface DocLead extends FMDoc {

	Integer getAccountId();

	void setAccountId(Integer id);

	ObjAccount getAccount();

	Integer getRefObjId();

	void setRefObjId(Integer objId);

	Obj getRefObj();

	Integer getRefDocId();

	void setRefDocId(Integer docId);

	Doc getRefDoc();

	String getSubject();

	void setSubject(String subject);

	String getDescription();

	void setDescription(String description);

	Set<CodeArea> getAreas();

	void clearAreas();

	void addArea(CodeArea area);

	void removeArea(CodeArea area);

	CodeLeadSource getLeadSource();

	void setLeadSource(CodeLeadSource leadSource);

	CodeLeadRating getLeadRating();

	void setLeadRating(CodeLeadRating leadRating);

	Integer getContactId();

	void setContactId(Integer id);

	ObjContact getContact();

	CodeSalutation getSalutation();

	void setSalutation(CodeSalutation salutation);

	CodeTitle getTitle();

	void setTitle(CodeTitle title);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getPhone();

	void setPhone(String phone);

	String getMobile();

	void setMobile(String mobile);

	String getEmail();

	void setEmail(String email);

	String getStreet();

	void setStreet(String street);

	String getZip();

	void setZip(String zip);

	String getCity();

	void setCity(String city);

	String getState();

	void setState(String state);

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

}
