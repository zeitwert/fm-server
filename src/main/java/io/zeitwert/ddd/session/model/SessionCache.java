package io.zeitwert.ddd.session.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

import java.util.List;

public interface SessionCache<A extends Aggregate> {

	boolean hasItem(SessionInfo sessionInfo, Integer id);

	A getItem(SessionInfo sessionInfo, Integer id);

	void addItem(SessionInfo sessionInfo, A aggregate);

	void removeItem(SessionInfo sessionInfo, Integer id);

	List<A> getItemList(Integer id);

}
