
package io.zeitwert.fm.obj.service.api;

import io.dddrive.ddd.service.api.AggregateCache;
import io.dddrive.obj.model.Obj;

import java.util.Map;

public interface ObjVCache extends AggregateCache<Obj> {

	Map<String, Integer> getStatistics();

}
