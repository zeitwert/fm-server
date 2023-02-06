
package io.zeitwert.fm.test.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface DocTest extends Doc, ItemWithNotes, ItemWithTasks {

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

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

	Integer getRefObjId();

	void setRefObjId(Integer id);

	ObjTest getRefObj();

	Integer getRefDocId();

	void setRefDocId(Integer id);

	DocTest getRefDoc();

	boolean hasCountry(CodeCountry country);

	Set<CodeCountry> getCountrySet();

	void clearCountrySet();

	void addCountry(CodeCountry country);

	void removeCountry(CodeCountry country);

	// Integer getNodeCount();

	// ObjTestPartNode getNode(Integer seqNr);

	// List<ObjTestPartNode> getNodeList();

	// ObjTestPartNode getNodeById(Integer nodeId);

	// void clearNodeList();

	// ObjTestPartNode addNode();

	// void removeNode(Integer nodeId);

}
