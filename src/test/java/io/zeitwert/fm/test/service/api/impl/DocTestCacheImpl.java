
package io.zeitwert.fm.test.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.service.api.DocTestCache;

@Service("docTestCache")
public class DocTestCacheImpl extends AggregateCacheBase<DocTest> implements DocTestCache {

	public DocTestCacheImpl(DocTestRepository repository) {
		super(repository, DocTest.class);
	}

}
