
package io.zeitwert.fm.account.model;

import java.math.BigDecimal;
import java.util.List;

import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.contact.model.ObjContact;
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocument;
// TODO-MIGRATION: Task - uncomment after Task is migrated
// import io.zeitwert.fm.task.model.ItemWithTasks;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;

// TODO-MIGRATION: Task - add "ItemWithTasks" after Task is migrated
public interface ObjAccount extends Obj, ItemWithNotes {

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

	BigDecimal getDiscountRate();

	void setDiscountRate(BigDecimal rate);

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// Integer getLogoImageId();

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// ObjDocument getLogoImage();

	Integer getMainContactId();

	void setMainContactId(Integer id);

	ObjContact getMainContact();

	List<ObjContact> getContacts();

}
