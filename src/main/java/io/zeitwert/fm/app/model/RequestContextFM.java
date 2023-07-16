package io.zeitwert.fm.app.model;

import io.dddrive.app.model.RequestContext;
import io.zeitwert.fm.oe.model.enums.CodeLocale;

public interface RequestContextFM extends RequestContext {

	CodeLocale getLocale();

	boolean hasAccount();

	Integer getAccountId();

}
