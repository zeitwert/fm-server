
package io.zeitwert.fm.obj.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.obj.model.Obj;

import java.util.Map;

public interface ObjVCache extends AggregateCache<Obj> {

	Map<String, Integer> getStatistics();

}
