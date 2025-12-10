package io.zeitwert.fm.test.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.test.model.enums.CodeTestType;

/**
 * Test Obj interface using the NEW dddrive framework (io.dddrive.core.*).
 */
public interface ObjTest extends Obj {

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

	boolean hasTestType(CodeTestType testType);

	Set<CodeTestType> getTestTypeSet();

	void clearTestTypeSet();

	void addTestType(CodeTestType testType);

	void removeTestType(CodeTestType testType);

	Integer getNodeCount();

	ObjTestPartNode getNode(Integer seqNr);

	List<ObjTestPartNode> getNodeList();

	ObjTestPartNode getNodeById(Integer nodeId);

	void clearNodeList();

	ObjTestPartNode addNode();

	void removeNode(Integer nodeId);

	// Note operations (implemented directly, bypassing mixin)
	List<ObjNote> getNotes();

	ObjNote addNote(CodeNoteType noteType);

	void removeNote(Object noteId);

}
