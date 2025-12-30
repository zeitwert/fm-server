package io.zeitwert.dddrive.app.model;

import dddrive.ddd.core.model.Aggregate;
import io.zeitwert.fm.oe.model.ObjUser;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface SessionContext {

	Object getUserId();

	ObjUser getUser();

	Object getTenantId();

	boolean hasAggregate(Object id);

	Aggregate getAggregate(Object id);

	void addAggregate(Object id, Aggregate aggregate);

	void clearAggregates();

	LocalDate getCurrentDate();

	OffsetDateTime getCurrentTime();

}
