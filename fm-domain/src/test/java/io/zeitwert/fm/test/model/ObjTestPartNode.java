package io.zeitwert.fm.test.model;

import io.dddrive.core.obj.model.ObjPart;
import io.zeitwert.fm.test.model.enums.CodeTestType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Test ObjPart interface using the NEW dddrive framework (io.dddrive.core.*).
 */
public interface ObjTestPartNode extends ObjPart<ObjTest> {

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

	Integer getRefTestId();

	void setRefTestId(Integer id);

	ObjTest getRefTest();

}
