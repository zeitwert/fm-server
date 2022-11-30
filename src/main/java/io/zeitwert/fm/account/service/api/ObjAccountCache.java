package io.zeitwert.fm.account.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.account.model.ObjAccount;

import java.util.Map;

public interface ObjAccountCache extends AggregateCache<ObjAccount> {

	Map<String, Integer> getStatistics();

}
