package io.zeitwert.ddd.part.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.base.PartStatus;
import io.zeitwert.ddd.session.model.SessionInfo;

public interface PartMeta<A extends Aggregate> {

	SessionInfo getSessionInfo();

	AppContext getAppContext();

	A getAggregate();

	PartStatus getStatus();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

}
