package io.zeitwert.fm.app.model;

import io.dddrive.app.model.RequestContext;

public interface RequestContextFM extends RequestContext {

	boolean hasAccount();

	Integer getAccountId();

}
