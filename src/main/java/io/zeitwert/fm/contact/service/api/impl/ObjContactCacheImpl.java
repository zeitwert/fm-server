package io.zeitwert.fm.contact.service.api.impl;

import org.springframework.stereotype.Service;

import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.service.api.ObjContactCache;

@Service("contactCache")
public class ObjContactCacheImpl extends AggregateCacheBase<ObjContact> implements ObjContactCache {

	public ObjContactCacheImpl(ObjContactRepository repository) {
		super(repository, ObjContact.class);
	}

}
