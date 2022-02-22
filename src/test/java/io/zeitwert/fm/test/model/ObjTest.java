package io.zeitwert.fm.test.model;

import io.zeitwert.fm.common.model.enums.CodeArea;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.ddd.common.model.enums.CodeCountry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ObjTest extends FMObj {

	@Override
	ObjTestRepository getRepository();

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

	Set<CodeArea> getAreaSet();

	void clearAreaSet();

	void addArea(CodeArea area);

	void removeArea(CodeArea area);

	Integer getNodeCount();

	ObjTestPartNode getNode(Integer seqNr);

	List<ObjTestPartNode> getNodeList();

	ObjTestPartNode getNodeById(Integer nodeId);

	void clearNodeList();

	ObjTestPartNode addNode();

	void removeNode(Integer nodeId);

}
