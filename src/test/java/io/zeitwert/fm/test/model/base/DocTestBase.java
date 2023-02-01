
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;

public abstract class DocTestBase extends FMDocBase implements DocTest {

	//@formatter:off
	protected final SimpleProperty<String> shortText = this.addSimpleProperty("shortText", String.class);
	protected final SimpleProperty<String> longText = this.addSimpleProperty("longText", String.class);
	protected final SimpleProperty<LocalDate> date = this.addSimpleProperty("date", LocalDate.class);
	protected final SimpleProperty<Integer> int_ = this.addSimpleProperty("int", Integer.class);
	protected final SimpleProperty<Boolean> isDone = this.addSimpleProperty("isDone", Boolean.class);
	protected final SimpleProperty<JSON> json = this.addSimpleProperty("json", JSON.class);
	protected final SimpleProperty<BigDecimal> nr = this.addSimpleProperty("nr", BigDecimal.class);
	protected final EnumProperty<CodeCountry> country = this.addEnumProperty("country", CodeCountry.class);
	protected final ReferenceProperty<ObjTest> refObj = this.addReferenceProperty("refObj", ObjTest.class);
	protected final ReferenceProperty<DocTest> refDoc = this.addReferenceProperty("refDoc", DocTest.class);
	protected final EnumSetProperty<CodeCountry> countrySet = this.addEnumSetProperty("countrySet", CodeCountry.class);
	// protected final PartListProperty<DocTestPartNode> nodeList;
	//@formatter:on

	protected DocTestBase(DocTestRepository repository, AggregateState state) {
		super(repository, state);
	}

	@Override
	public DocTestRepository getRepository() {
		return (DocTestRepository) super.getRepository();
	}

	@Override
	public void doInitWorkflow() {
		CodeCaseStage initStage = CodeCaseStageEnum.getCaseStage("test.new");
		this.doInitWorkflow("test", initStage);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartItemRepository itemRepo = this.getRepository().getItemRepository();
		CodePartListType countrySetType = CodePartListTypeEnum.getPartListType("test.countrySet");
		this.countrySet.loadEnums(itemRepo.getParts(this, countrySetType));
		// ObjTestPartNodeRepository nodeRepo =
		// this.getRepository().getNodeRepository();
		// this.nodeList.loadParts(nodeRepo.getParts(this,
		// this.getRepository().getNodeListType()));
	}

	@Override
	public void doCalcSearch() {
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.countrySet)) {
			return this.getRepository().getItemRepository().create(this, partListType);
		}
		// if (property.equals(this.nodeList)) {
		// return (P) this.getRepository().getNodeRepository().create(this,
		// partListType);
		// }
		return super.addPart(property, partListType);
	}

	@Override
	public String getJson() {
		return this.json.getValue() == null ? null : this.json.getValue().toString();
	}

	@Override
	public void setJson(String json) {
		this.json.setValue(json == null ? null : JSON.valueOf(json));
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(
				"[" + this.getString(this.getShortText()) + ", " + this.getString(this.getLongText()) + "]"
						+ (this.getRefObjId() == null ? "" : " (RefObj:" + this.getString(this.getRefObj().getCaption()) + ")")
						+ (this.getRefDocId() == null ? "" : " (RefDoc:" + this.getString(this.getRefDoc().getCaption()) + ")"));
	}

	private String getString(String s) {
		return s == null ? "" : s;
	}

}
