
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.JSON;

public abstract class DocTestBase extends FMDocBase
		implements DocTest, AggregateWithNotesMixin, AggregateWithTasksMixin {

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

	protected DocTestBase(DocTestRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public DocTestRepository getRepository() {
		return (DocTestRepository) super.getRepository();
	}

	@Override
	public DocTest aggregate() {
		return this;
	}

	@Override
	public final ObjAccount getAccount() {
		return this.getAppContext().getBean(ObjAccountCache.class).get(this.getAccountId());
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
