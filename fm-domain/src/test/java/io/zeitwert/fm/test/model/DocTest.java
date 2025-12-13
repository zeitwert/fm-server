package io.zeitwert.fm.test.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import io.dddrive.core.doc.model.Doc;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.test.model.enums.CodeTestType;

/**
 * Test Doc interface using the NEW dddrive framework (io.dddrive.core.*).
 * <p>
 * Note: getAccountId() and setAccountId() are NOT declared here because
 * they are inherited from FMDocCoreBase (Kotlin property accessors).
 * Declaring them would cause a JVM signature clash with Kotlin.
 * Use ((FMDocCoreBase) docTest).getAccountId() to access this property.
 */
public interface DocTest extends Doc, ItemWithNotes {

	ObjAccount getAccount();

	String getShortText();

	void setShortText(String shortText);

	String getLongText();

	void setLongText(String longText);

	LocalDate getDate();

	void setDate(LocalDate date);

	Integer getInt();

	void setInt(Integer i);

	Boolean getIsDone();

	void setIsDone(Boolean isDone);

	String getJson();

	void setJson(String json);

	BigDecimal getNr();

	void setNr(BigDecimal nr);

	CodeTestType getTestType();

	void setTestType(CodeTestType testType);

	Integer getRefObjId();

	void setRefObjId(Integer id);

	ObjTest getRefObj();

	Integer getRefDocId();

	void setRefDocId(Integer id);

	DocTest getRefDoc();

	boolean hasTestType(CodeTestType testType);

	Set<CodeTestType> getTestTypeSet();

	void clearTestTypeSet();

	void addTestType(CodeTestType testType);

	void removeTestType(CodeTestType testType);

}
