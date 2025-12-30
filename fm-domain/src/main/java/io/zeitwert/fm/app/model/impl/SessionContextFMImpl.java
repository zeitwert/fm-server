package io.zeitwert.fm.app.model.impl;

import io.zeitwert.dddrive.app.model.base.SessionContextBase;
import io.zeitwert.fm.app.model.SessionContextFM;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class SessionContextFMImpl extends SessionContextBase implements SessionContextFM {

	private final CodeLocale locale;
	private final Integer accountId;

	@Override
	public boolean hasAccount() {
		return this.accountId != null;
	}

}
