package io.zeitwert.fm.test.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import io.dddrive.obj.model.Obj;
import io.dddrive.oe.model.enums.CodeCountry;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface ObjTest extends Obj, ItemWithNotes, ItemWithTasks {

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

	Integer getRefTestId();

	void setRefTestId(Integer id);

	ObjTest getRefTest();

	boolean hasCountry(CodeCountry country);

	Set<CodeCountry> getCountrySet();

	void clearCountrySet();

	void addCountry(CodeCountry country);

	void removeCountry(CodeCountry country);

	Integer getNodeCount();

	ObjTestPartNode getNode(Integer seqNr);

	List<ObjTestPartNode> getNodeList();

	ObjTestPartNode getNodeById(Integer nodeId);

	void clearNodeList();

	ObjTestPartNode addNode();

	void removeNode(Integer nodeId);

}
