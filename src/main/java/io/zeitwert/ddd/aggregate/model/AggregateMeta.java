
package io.zeitwert.ddd.aggregate.model;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.model.AggregatePartValidation;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta {

	CodeAggregateType getAggregateType();

	SessionInfo getSessionInfo();

	AppContext getAppContext();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

	List<AggregatePartValidation> getValidationList();

}
