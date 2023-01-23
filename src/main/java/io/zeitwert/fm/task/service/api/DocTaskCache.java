
package io.zeitwert.fm.task.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.task.model.DocTask;

import java.util.Map;

public interface DocTaskCache extends AggregateCache<DocTask> {

	Map<String, Integer> getStatistics();

}
