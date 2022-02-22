package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.common.model.enums.CodeArea;
import io.zeitwert.fm.common.model.enums.CodeAreaEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.common.model.enums.CodeCountry;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

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
	protected final EnumSetProperty<CodeArea> areaSet;
	protected final PartListProperty<ObjTestPartNode> nodeList;

	protected ObjTestBase(SessionInfo sessionInfo, ObjTestRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> testRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = testRecord;
		this.shortText = this.addSimpleProperty(dbRecord, ObjTestFields.SHORT_TEXT);
		this.longText = this.addSimpleProperty(dbRecord, ObjTestFields.LONG_TEXT);
		this.date = this.addSimpleProperty(dbRecord, ObjTestFields.DATE);
		this.int_ = this.addSimpleProperty(dbRecord, ObjTestFields.INT);
		this.isDone = this.addSimpleProperty(dbRecord, ObjTestFields.IS_DONE);
		this.json = this.addSimpleProperty(dbRecord, ObjTestFields.JSON);
		this.nr = this.addSimpleProperty(dbRecord, ObjTestFields.NR);
		this.country = this.addEnumProperty(dbRecord, ObjTestFields.COUNTRY_ID, CodeCountryEnum.class);
		this.refTest = this.addReferenceProperty(dbRecord, ObjTestFields.REF_TEST_ID, ObjTest.class);
		this.areaSet = this.addEnumSetProperty(this.getRepository().getAreaSetType(), CodeAreaEnum.class);
		this.nodeList = this.addPartListProperty(this.getRepository().getNodeListType());
	}

	@Override
	public ObjTestRepository getRepository() {
		return (ObjTestRepository) super.getRepository();
	}

	public abstract void loadAreaSet(Collection<ObjPartItem> areaSet);

	public abstract void loadNodeList(Collection<ObjTestPartNode> nodeList);

	@Override
	public void doInit(Integer objId, Integer tenantId, Integer userId) {
		super.doInit(objId, tenantId, userId);
		this.dbRecord.setValue(ObjTestFields.OBJ_ID, objId);
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
	public void doStore(Integer userId) {
		super.doStore(userId);
		this.dbRecord.store();
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
		this.calcCaption();
	}

	private void calcCaption() {
		this.caption.setValue("[" + this.getString(this.getShortText()) + ", "
				+ this.getString(this.getLongText()) + "]"
				+ (this.getRefTestId() == null ? "" : " (" + this.getString(this.getRefTest().getCaption()) + ")"));
	}

	private String getString(String s) {
		return s == null ? "" : s;
	}

	@Override
	public void beforeStore() {
		super.beforeStore();
		this.areaSet.beforeStore();
	}

}
