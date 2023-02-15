
package io.zeitwert.fm.doc.service.api;

import io.dddrive.ddd.service.api.AggregateCache;
import io.dddrive.doc.model.Doc;

import java.util.Map;

public interface DocVCache extends AggregateCache<Doc> {

	Map<String, Integer> getStatistics();

}
