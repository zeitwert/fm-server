package fm.comunas.fm.test.model.base;

import fm.comunas.ddd.common.model.enums.CodeCountry;
import fm.comunas.ddd.common.model.enums.CodeCountryEnum;
import fm.comunas.ddd.obj.model.base.ObjPartBase;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.fm.test.model.ObjTest;
import fm.comunas.fm.test.model.ObjTestPartNode;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class ObjTestPartNodeBase extends ObjPartBase<ObjTest> implements ObjTestPartNode {

	protected final SimpleProperty<String> shortText;
	protected final SimpleProperty<String> longText;
	protected final SimpleProperty<LocalDate> date;
	protected final SimpleProperty<Integer> int_;
	protected final SimpleProperty<Boolean> isDone;
	protected final SimpleProperty<JSON> json;
	protected final SimpleProperty<BigDecimal> nr;
	protected final EnumProperty<CodeCountry> country;
	protected final ReferenceProperty<ObjTest> refTest;

	public ObjTestPartNodeBase(ObjTest obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
		this.shortText = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.SHORT_TEXT);
		this.longText = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.LONG_TEXT);
		this.date = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.DATE);
		this.int_ = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.INT);
		this.isDone = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.IS_DONE);
		this.json = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.JSON);
		this.nr = this.addSimpleProperty(dbRecord, ObjTestPartNodeFields.NR);
		this.country = this.addEnumProperty(dbRecord, ObjTestPartNodeFields.COUNTRY_ID, CodeCountryEnum.class);
		this.refTest = this.addReferenceProperty(dbRecord, ObjTestPartNodeFields.REF_TEST_ID, ObjTest.class);
	}

	@Override
	public String getJson() {
		return this.json.getValue() == null ? null : this.json.getValue().toString();
	}

	@Override
	public void setJson(String json) {
		this.json.setValue(JSON.valueOf(json));
	}

}
