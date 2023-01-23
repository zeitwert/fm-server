
package io.zeitwert.ddd.aggregate.model;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.ddd.validation.model.AggregatePartValidation;

/**
 * A DDD Aggregate Root Meta Information.
 */
public interface AggregateMeta {

	AggregateRepository<? extends Aggregate, ? extends Record> getRepository();

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
