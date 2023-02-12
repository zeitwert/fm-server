
package io.dddrive.app.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.enums.CodeLocale;

public interface RequestContext {

	Integer getTenantId();

	ObjUser getUser();

	CodeLocale getLocale();

	boolean hasAggregate(Integer id);

	Aggregate getAggregate(Integer id);

	void addAggregate(Aggregate aggregate);

	LocalDate getCurrentDate();

	OffsetDateTime getCurrentTime();

}
