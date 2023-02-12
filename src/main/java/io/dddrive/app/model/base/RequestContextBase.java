
package io.dddrive.app.model.base;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.enums.CodeLocale;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class RequestContextBase implements RequestContext {

	private final ObjUser user;
	private final Integer tenantId;
	private final CodeLocale locale;
	private final Map<Integer, Aggregate> aggregates = new ConcurrentHashMap<>();

	public boolean hasAggregate(Integer id) {
		return this.aggregates.containsKey(id);
	}

	public Aggregate getAggregate(Integer id) {
		return this.aggregates.get(id);
	}

	public void addAggregate(Aggregate aggregate) {
		this.aggregates.put(aggregate.getId(), aggregate);
	}

	public LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	public OffsetDateTime getCurrentTime() {
		return OffsetDateTime.now();
	}

}
