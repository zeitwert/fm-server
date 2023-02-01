
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class DocTestBase extends FMDocBase implements DocTest {

	protected final SimpleProperty<String> shortText;
	protected final SimpleProperty<String> longText;
	protected final SimpleProperty<LocalDate> date;
	protected final SimpleProperty<Integer> int_;
	protected final SimpleProperty<Boolean> isDone;
	protected final SimpleProperty<JSON> json;
	protected final SimpleProperty<BigDecimal> nr;
	protected final EnumProperty<CodeCountry> country;
	protected final ReferenceProperty<ObjTest> refObj;
	protected final ReferenceProperty<DocTest> refDoc;
	protected final EnumSetProperty<CodeCountry> countrySet;
	// protected final PartListProperty<DocTestPartNode> nodeList;

	protected DocTestBase(DocTestRepository repository, UpdatableRecord<?> docRecord, UpdatableRecord<?> testRecord) {
		super(repository, docRecord, testRecord);
		this.shortText = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.SHORT_TEXT);
		this.longText = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.LONG_TEXT);
		this.date = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.DATE);
		this.int_ = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.INT);
		this.isDone = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.IS_DONE);
		this.json = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.JSON);
		this.nr = this.addSimpleProperty(this.extnDbRecord(), DocTestFields.NR);
		this.country = this.addEnumProperty(this.extnDbRecord(), DocTestFields.COUNTRY_ID, CodeCountryEnum.class);
		this.refObj = this.addReferenceProperty(this.extnDbRecord(), DocTestFields.REF_OBJ_ID, ObjTest.class);
		this.refDoc = this.addReferenceProperty(this.extnDbRecord(), DocTestFields.REF_DOC_ID, DocTest.class);
		this.countrySet = this.addEnumSetProperty(this.getRepository().getCountrySetType(), CodeCountryEnum.class);
		// this.nodeList =
		// this.addPartListProperty(this.getRepository().getNodeListType());
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
		this.countrySet.loadEnums(itemRepo.getParts(this, this.getRepository().getCountrySetType()));
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
