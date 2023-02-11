
package io.zeitwert.ddd.oe.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeLocaleEnum")
public class CodeLocaleEnum extends EnumerationBase<CodeLocale> {

	private static CodeLocaleEnum INSTANCE;

	protected CodeLocaleEnum(Enumerations enums) {
		super(CodeLocale.class, enums);
		INSTANCE = this;
	}

	public static CodeLocaleEnum getInstance() {
		return INSTANCE;
	}

	@Override
	public void addItem(CodeLocale item) {
		super.addItem(item);
	}

	public static CodeLocale getLocale(String localeId) {
		return INSTANCE.getItem(localeId);
	}

}
