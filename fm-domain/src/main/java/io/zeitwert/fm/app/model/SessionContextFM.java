package io.zeitwert.fm.app.model;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.oe.model.enums.CodeLocale;

public interface SessionContextFM extends SessionContext {

	CodeLocale getLocale();

	boolean hasAccount();

	Integer getAccountId();

}
