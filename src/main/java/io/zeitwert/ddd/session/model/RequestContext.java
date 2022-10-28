
package io.zeitwert.ddd.session.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeLocale;

public class RequestContext {

	private final ObjUser user;
	private final Integer accountId;
	private final CodeLocale locale;
	private final Map<Integer, Aggregate> aggregates = new ConcurrentHashMap<>();

	public RequestContext(ObjUser user, Integer accountId, CodeLocale locale) {
		this.user = user;
		this.accountId = accountId;
		this.locale = locale;
	}

	public ObjTenant getTenant() {
		return this.getUser().getTenant();
	}

	public ObjUser getUser() {
		return this.user;
	}

	public boolean hasAccount() {
		return this.accountId != null;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public CodeLocale getLocale() {
		return this.locale;
	}

	public boolean hasAggregate(Integer id) {
		return this.aggregates.containsKey(id);
	}

	public Aggregate getAggregate(Integer id) {
		return this.aggregates.get(id);
	}

	public Aggregate addAggregate(Aggregate aggregate) {
		return this.aggregates.put(aggregate.getId(), aggregate);
	}

	public LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	public OffsetDateTime getCurrentTime() {
		return OffsetDateTime.now();
	}

	@Override
	public String toString() {
		return "RequestContext("
				+ "user: " + (user != null ? user.getId() : "null")
				+ ", accountId: " + accountId
				+ ", locale: " + (locale != null ? locale.getId() : "null")
				+ ")";
	}

}
