
package io.dddrive.ddd.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.validation.model.AggregatePartValidation;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta {

	AggregateRepository<?, ?> getRepository();

	CodeAggregateType getAggregateType();

	RequestContext getRequestContext();

	AppContext getAppContext();

	Integer getVersion();

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

	List<AggregatePartValidation> getValidations();

	List<String> getOperations();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

}
