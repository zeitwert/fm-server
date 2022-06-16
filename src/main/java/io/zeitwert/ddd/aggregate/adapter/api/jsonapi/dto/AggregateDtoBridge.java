package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.TableRecord;

public abstract class AggregateDtoBridge<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>> {

	protected static final <Aggr extends Aggregate> AggregateRepository<Aggr, ?> getRepository(Class<Aggr> aggrClass) {
		return AppContext.getInstance().getRepository(aggrClass);
	}

	private static ObjUserRepository userRepository = null;

	protected ObjUserRepository getUserRepository() {
		if (userRepository == null) {
			userRepository = (ObjUserRepository) getRepository(ObjUser.class);
		}
		return userRepository;
	}

	public abstract void toAggregate(D dto, A aggregate);

	public abstract D fromAggregate(A aggregate, SessionInfo sessionInfo);

	public abstract D fromRecord(V obj, SessionInfo sessionInfo);

}
