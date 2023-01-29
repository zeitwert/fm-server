package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class ObjTestBase extends FMObjBase implements ObjTest {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> shortText;
	protected final SimpleProperty<String> longText;
	protected final SimpleProperty<LocalDate> date;
	protected final SimpleProperty<Integer> int_;
	protected final SimpleProperty<Boolean> isDone;
	protected final SimpleProperty<JSON> json;
	protected final SimpleProperty<BigDecimal> nr;
	protected final EnumProperty<CodeCountry> country;
	protected final ReferenceProperty<ObjTest> refTest;
	protected final PartListProperty<ObjTestPartNode> nodeList;

	protected ObjTestBase(ObjTestRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> testRecord) {
		super(repository, objRecord);
		this.dbRecord = testRecord;
		this.shortText = this.addSimpleProperty(this.dbRecord, ObjTestFields.SHORT_TEXT);
		this.longText = this.addSimpleProperty(this.dbRecord, ObjTestFields.LONG_TEXT);
		this.date = this.addSimpleProperty(this.dbRecord, ObjTestFields.DATE);
		this.int_ = this.addSimpleProperty(this.dbRecord, ObjTestFields.INT);
		this.isDone = this.addSimpleProperty(this.dbRecord, ObjTestFields.IS_DONE);
		this.json = this.addSimpleProperty(this.dbRecord, ObjTestFields.JSON);
		this.nr = this.addSimpleProperty(this.dbRecord, ObjTestFields.NR);
		this.country = this.addEnumProperty(this.dbRecord, ObjTestFields.COUNTRY_ID, CodeCountryEnum.class);
		this.refTest = this.addReferenceProperty(this.dbRecord, ObjTestFields.REF_TEST_ID, ObjTest.class);
		this.nodeList = this.addPartListProperty(this.getRepository().getNodeListType());
	}

	@Override
	public ObjTestRepository getRepository() {
		return (ObjTestRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjTestFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjTestFields.TENANT_ID, tenantId);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjTestPartNodeRepository nodeRepo = this.getRepository().getNodeRepository();
		this.nodeList.loadParts(nodeRepo.getParts(this, this.getRepository().getNodeListType()));

	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public void doCalcSearch() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.nodeList) {
			return (P) this.getRepository().getNodeRepository().create(this, partListType);
		}
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
		this.setCaption("[" + this.getString(this.getShortText()) + ", "
				+ this.getString(this.getLongText()) + "]"
				+ (this.getRefTestId() == null ? "" : " (" + this.getString(this.getRefTest().getCaption()) + ")"));
	}

	private String getString(String s) {
		return s == null ? "" : s;
	}

}
