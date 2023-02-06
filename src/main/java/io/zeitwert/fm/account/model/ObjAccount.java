
package io.zeitwert.fm.account.model;

import java.math.BigDecimal;
import java.util.List;

import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.dms.model.ObjDocument;
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

	BigDecimal getInflationRate();

	void setInflationRate(BigDecimal rate);

	Integer getLogoImageId();

	ObjDocument getLogoImage();

	Integer getMainContactId();

	void setMainContactId(Integer id);

	ObjContact getMainContact();

	List<ObjContact> getContacts();

}
