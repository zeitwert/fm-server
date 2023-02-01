
package io.zeitwert.fm.test.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.test.model.DocTest;

import java.util.Map;

public interface DocTestCache extends AggregateCache<DocTest> {

	Map<String, Integer> getStatistics();

}
