
package io.dddrive.oe.model.enums;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.base.EnumerationBase;

@Component("codeCountryEnum")
public class CodeCountryEnum extends EnumerationBase<CodeCountry> {

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
