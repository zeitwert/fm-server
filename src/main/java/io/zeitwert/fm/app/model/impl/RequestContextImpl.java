package io.zeitwert.fm.app.model.impl;

import io.dddrive.app.model.base.RequestContextBase;
import io.zeitwert.fm.app.model.RequestContextFM;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class RequestContextImpl extends RequestContextBase implements RequestContextFM {

	private final Integer accountId;

	public boolean hasAccount() {
		return this.accountId != null;
	}

}
