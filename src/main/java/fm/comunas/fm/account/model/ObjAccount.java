
package fm.comunas.fm.account.model;

import java.util.List;
import java.util.Set;

import fm.comunas.fm.account.model.enums.CodeClientSegment;
import fm.comunas.fm.account.model.enums.CodeAccountType;
import fm.comunas.fm.common.model.enums.CodeArea;
import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.obj.model.FMObj;
import fm.comunas.ddd.common.model.enums.CodeCurrency;

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
