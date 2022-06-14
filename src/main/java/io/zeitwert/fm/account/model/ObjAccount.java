
package io.zeitwert.fm.account.model;

import java.util.List;
import java.util.Set;

import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.account.model.enums.CodeArea;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.account.model.enums.CodeCurrency;

public interface ObjAccount extends FMObj {

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	CodeAccountType getAccountType();

	void setAccountType(CodeAccountType accountType);

	CodeClientSegment getClientSegment();

	void setClientSegment(CodeClientSegment clientSegment);

	CodeCurrency getReferenceCurrency();

	void setReferenceCurrency(CodeCurrency currency);

	Set<CodeArea> getAreaSet();

	void clearAreaSet();

	void addArea(CodeArea area);

	void removeArea(CodeArea area);

	Integer getMainContactId();

	void setMainContactId(Integer id);

	ObjContact getMainContact();

	List<ObjContact> getContacts();

}
