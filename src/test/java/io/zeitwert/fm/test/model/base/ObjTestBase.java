
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;

public abstract class ObjTestBase extends ObjExtnBase
		implements ObjTest, AggregateWithNotesMixin, AggregateWithTasksMixin {

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
	protected final PartListProperty<ObjTestPartNode> nodes = this.addPartListProperty("nodeList", ObjTestPartNode.class);

	protected ObjTestBase(ObjTestRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjTestRepository getRepository() {
		return (ObjTestRepository) super.getRepository();
	}

	@Override
	public ObjTest aggregate() {
		return this;
	}

	@Override
	public void doCalcSearch() {
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
