
package io.zeitwert.fm.obj.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.service.api.ObjVCache;

@Service("objCache")
public class ObjVCacheImpl extends AggregateCacheBase<Obj> implements ObjVCache {

	public ObjVCacheImpl(ObjVRepository repository) {
		super(repository, Obj.class);
	}

}
