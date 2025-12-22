
package io.zeitwert.dddrive.app.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.oe.model.ObjUser;

public interface RequestContext {

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
