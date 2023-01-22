package io.zeitwert.fm.contact.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.contact.model.ObjContact;

import java.util.Map;

public interface ObjContactCache extends AggregateCache<ObjContact> {

  Map<String, Integer> getStatistics();

}
