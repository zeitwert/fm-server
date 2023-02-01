
package io.zeitwert.fm.test.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.test.model.ObjTest;

import java.util.Map;

public interface ObjTestCache extends AggregateCache<ObjTest> {

	Map<String, Integer> getStatistics();

}
