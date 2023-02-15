
package io.zeitwert.fm.doc.service.api.impl;

import org.springframework.stereotype.Service;

import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.dddrive.doc.model.Doc;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.doc.service.api.DocVCache;

@Service("docCache")
public class DocVCacheImpl extends AggregateCacheBase<Doc> implements DocVCache {

	public DocVCacheImpl(DocVRepository repository) {
		super(repository, Doc.class);
	}

}
