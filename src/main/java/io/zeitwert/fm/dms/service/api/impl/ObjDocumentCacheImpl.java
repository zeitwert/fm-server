package io.zeitwert.fm.dms.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.service.api.ObjDocumentCache;

@Service("documentCache")
public class ObjDocumentCacheImpl extends AggregateCacheBase<ObjDocument> implements ObjDocumentCache {

	public ObjDocumentCacheImpl(ObjDocumentRepository repository) {
		super(repository, ObjDocument.class);
	}

}
