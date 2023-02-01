
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class ObjTestBase extends FMObjBase implements ObjTest {

	protected final SimpleProperty<String> shortText = this.addSimpleProperty("shortText", String.class);
	protected final SimpleProperty<String> longText = this.addSimpleProperty("longText", String.class);
	protected final SimpleProperty<LocalDate> date = this.addSimpleProperty("date", LocalDate.class);
	protected final SimpleProperty<Integer> int_ = this.addSimpleProperty("int", Integer.class);
	protected final SimpleProperty<Boolean> isDone = this.addSimpleProperty("isDone", Boolean.class);
	protected final SimpleProperty<JSON> json = this.addSimpleProperty("json", JSON.class);
	protected final SimpleProperty<BigDecimal> nr = this.addSimpleProperty("nr", BigDecimal.class);
	protected final EnumProperty<CodeCountry> country = this.addEnumProperty("country", CodeCountry.class);
	protected final ReferenceProperty<ObjTest> refTest = this.addReferenceProperty("refTest", ObjTest.class);
	protected final EnumSetProperty<CodeCountry> countries = this.addEnumSetProperty("countrySet", CodeCountry.class);
	protected final PartListProperty<ObjTestPartNode> nodeList = this.addPartListProperty("nodeList",
			ObjTestPartNode.class);

	protected ObjTestBase(ObjTestRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> testRecord) {
		super(repository, objRecord, testRecord);
	}

	@Override
	public ObjTestRepository getRepository() {
		return (ObjTestRepository) super.getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.countries.loadEnums(itemRepo.getParts(this, this.getRepository().getCountrySetType()));
		ObjTestPartNodeRepository nodeRepo = this.getRepository().getNodeRepository();
		this.nodeList.loadParts(nodeRepo.getParts(this, this.getRepository().getNodeListType()));
	}

	@Override
	public void doCalcSearch() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property.equals(this.nodeList)) {
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
