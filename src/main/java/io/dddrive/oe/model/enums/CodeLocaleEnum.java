
package io.dddrive.oe.model.enums;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.enums.model.base.EnumerationBase;

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
