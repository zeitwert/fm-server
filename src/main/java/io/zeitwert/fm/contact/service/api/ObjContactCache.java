package io.zeitwert.fm.contact.service.api;

import io.dddrive.ddd.service.api.AggregateCache;
import io.zeitwert.fm.contact.model.ObjContact;

import java.util.Map;

public interface ObjContactCache extends AggregateCache<ObjContact> {

	Map<String, Integer> getStatistics();

}
