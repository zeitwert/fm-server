package io.zeitwert.fm.collaboration.service.api.impl;

import org.springframework.stereotype.Service;

import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.service.api.ObjNoteCache;

@Service("objNoteCache")
public class ObjNoteCacheImpl extends AggregateCacheBase<ObjNote> implements ObjNoteCache {

	public ObjNoteCacheImpl(ObjNoteRepository repository) {
		super(repository, ObjNote.class);
	}

}
