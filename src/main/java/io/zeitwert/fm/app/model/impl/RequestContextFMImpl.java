package io.zeitwert.fm.app.model.impl;

import io.dddrive.app.model.base.RequestContextBase;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class RequestContextFMImpl extends RequestContextBase implements RequestContextFM {

	private final CodeLocale locale;
	private final Integer accountId;

	@Override
	public boolean hasAccount() {
		return this.accountId != null;
	}

}
