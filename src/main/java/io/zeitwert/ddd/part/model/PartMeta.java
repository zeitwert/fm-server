package io.zeitwert.ddd.part.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.session.model.RequestContext;

public interface PartMeta<A extends Aggregate> {

	PartRepository<A, ?> getRepository();

	RequestContext getRequestContext();

	AppContext getAppContext();

	A getAggregate();

	Integer getParentPartId();

	String getPartListTypeId();

	Integer getSeqNr();

	PartPersistenceStatus getPersistenceStatus();

	boolean isDeleted();

	boolean isCalcEnabled();

	void disableCalc();

	void enableCalc();

}
