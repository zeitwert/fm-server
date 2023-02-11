
package io.zeitwert.ddd.oe.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeCountryEnum")
public class CodeCountryEnum extends EnumerationBase<CodeCountry> {

	static public final String TABLE_NAME = "code_country";

	private static CodeCountryEnum INSTANCE;

	protected CodeCountryEnum(Enumerations enums) {
		super(CodeCountry.class, enums);
		INSTANCE = this;
	}

	public static CodeCountryEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodeCountry item) {
		super.addItem(item);
	}

	public static CodeCountry getCountry(String countryId) {
		return INSTANCE.getItem(countryId);
	}

}
