
package io.zeitwert.ddd.session.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.fm.account.model.enums.CodeLocale;

public class RequestContext {

	private final ObjUser user;
	private final Integer tenantId;
	private final Integer accountId;
	private final CodeLocale locale;
	private final Map<Integer, Aggregate> aggregates = new ConcurrentHashMap<>();

	public RequestContext(ObjUser user, Integer tenantId, Integer accountId, CodeLocale locale) {
		this.user = user;
		this.tenantId = tenantId;
		this.accountId = accountId;
		this.locale = locale;
	}

	public ObjUser getUser() {
		return this.user;
	}

	public Integer getTenantId() {
		return this.tenantId;
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
				+ "user: " + (this.user != null ? this.user.getId() : "null")
				+ ", accountId: " + this.accountId
				+ ", locale: " + (this.locale != null ? this.locale.getId() : "null")
				+ ")";
	}

}
