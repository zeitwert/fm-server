package io.zeitwert.dddrive.app.model.base;

import dddrive.ddd.core.model.Aggregate;
import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.oe.model.ObjUser;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data()
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public abstract class SessionContextBase implements SessionContext {

	private final ObjUser user;
	private final Object tenantId;
	private final Map<Object, Aggregate> aggregates = new ConcurrentHashMap<>();

	public SessionContextBase(ObjUser user, Object tenantId) {
		this.user = user;
		this.tenantId = tenantId;
	}

	@Override
	public Object getUserId() {
		return this.user.getId();
	}

	@Override
	public ObjUser getUser() {
		return this.user;
	}

	@Override
	public Object getTenantId() {
		return this.tenantId;
	}

	@Override
	public boolean hasAggregate(Object id) {
		return this.aggregates.containsKey(id);
	}

	@Override
	public Aggregate getAggregate(Object id) {
		return this.aggregates.get(id);
	}

	@Override
	public void addAggregate(Object id, Aggregate aggregate) {
		this.aggregates.put(id, aggregate);
	}

	@Override
	public void clearAggregates() {
		this.aggregates.clear();
	}

	@Override
	public LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	@Override
	public OffsetDateTime getCurrentTime() {
		return OffsetDateTime.now();
	}

}
