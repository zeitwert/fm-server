package fm.comunas.fm.test.model;

import fm.comunas.ddd.common.model.enums.CodeCountry;
import fm.comunas.ddd.obj.model.ObjPart;

import java.math.BigDecimal;
import java.time.LocalDate;
// import java.util.List;

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

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

	Integer getRefTestId();

	void setRefTestId(Integer id);

	ObjTest getRefTest();

	// Integer getNodeCount();

	// ObjTestPartNode getNode(Integer seqNr);

	// List<ObjTestPartNode> getNodeList();

	// ObjTestPartNode getNodeById(Integer nodeId);

	// void clearNodeList();

	// ObjTestPartNode addNode();

	// void removeNode(Integer nodeId);

}
