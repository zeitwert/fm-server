
package io.zeitwert.fm.test.service.api.impl;

import org.springframework.stereotype.Service;

import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.service.api.ObjTestCache;

@Service("objTestCache")
public class ObjTestCacheImpl extends AggregateCacheBase<ObjTest> implements ObjTestCache {

	public ObjTestCacheImpl(ObjTestRepository repository) {
		super(repository, ObjTest.class);
	}

}
