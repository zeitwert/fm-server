package io.zeitwert.fm.test.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;

public abstract class ObjTestPartNodeBase extends ObjPartBase<ObjTest> implements ObjTestPartNode {

	protected final SimpleProperty<String> shortText = this.addSimpleProperty("shortText", String.class);
	protected final SimpleProperty<String> longText = this.addSimpleProperty("longText", String.class);
	protected final SimpleProperty<LocalDate> date = this.addSimpleProperty("date", LocalDate.class);
	protected final SimpleProperty<Integer> int_ = this.addSimpleProperty("int", Integer.class);
	protected final SimpleProperty<Boolean> isDone = this.addSimpleProperty("isDone", Boolean.class);
	protected final SimpleProperty<JSON> json = this.addSimpleProperty("json", JSON.class);
	protected final SimpleProperty<BigDecimal> nr = this.addSimpleProperty("nr", BigDecimal.class);
	protected final EnumProperty<CodeCountry> country = this.addEnumProperty("country", CodeCountry.class);
	protected final ReferenceProperty<ObjTest> refTest = this.addReferenceProperty("refTest", ObjTest.class);

	public ObjTestPartNodeBase(PartRepository<ObjTest, ?> repository, ObjTest obj, PartState state) {
		super(repository, obj, state);
	}

	@Override
	public String getJson() {
		JSON json = this.json.getValue();
		return json == null ? null : json.toString();
	}

	@Override
	public void setJson(String json) {
		this.json.setValue(JSON.valueOf(json));
	}

}
