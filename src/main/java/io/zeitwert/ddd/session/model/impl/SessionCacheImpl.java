package io.zeitwert.ddd.session.model.impl;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.session.model.SessionCache;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionCacheImpl<A extends Aggregate> implements SessionCache<A> {

	private final Map<SessionInfo, Map<Integer, A>> sessionMap = new ConcurrentHashMap<>();

	@Override
	public boolean hasItem(SessionInfo sessionInfo, Integer id) {
		if (!sessionMap.containsKey(sessionInfo)) {
			return false;
		}
		return sessionMap.get(sessionInfo).containsKey(id);
	}

	@Override
	public A getItem(SessionInfo sessionInfo, Integer id) {
		if (!sessionMap.containsKey(sessionInfo)) {
			return null;
		}
		return sessionMap.get(sessionInfo).get(id);
	}

	@Override
	public void addItem(SessionInfo sessionInfo, A aggregate) {
		if (!sessionMap.containsKey(sessionInfo)) {
			System.out.println(
					"cache.create "
							+ " " + System.identityHashCode(sessionInfo)
							+ " " + sessionInfo.getClass().getSimpleName()
							+ " " + (sessionInfo.getUser() != null ? sessionInfo.getUser().getEmail() : "[no user]")
							+ " " + aggregate.getMeta().getAggregateType().getId());
			sessionMap.put(sessionInfo, new ConcurrentHashMap<>());
		}
		// System.out.println(
		// "cache.addItem"
		// + " " + System.identityHashCode(sessionInfo)
		// + " " + aggregate.getMeta().getAggregateType().getId()
		// + " " + aggregate.getId());
		sessionMap.get(sessionInfo).put(aggregate.getId(), aggregate);
	}

	@Override
	public void removeItem(SessionInfo sessionInfo, Integer id) {
		if (sessionMap.containsKey(sessionInfo)) {
			sessionMap.get(sessionInfo).remove(id);
		}
	}

	@Override
	public List<A> getItemList(Integer id) {
		List<A> itemList = new ArrayList<>();
		for (SessionInfo s : this.sessionMap.keySet()) {
			A item = this.sessionMap.get(s).get(id);
			if (item != null) {
				itemList.add(item);
			}
		}
		return itemList;
	}

}
