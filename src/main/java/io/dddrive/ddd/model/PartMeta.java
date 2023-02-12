package io.dddrive.ddd.model;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContext;

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
