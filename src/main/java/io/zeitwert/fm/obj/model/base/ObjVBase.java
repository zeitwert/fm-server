package io.zeitwert.fm.obj.model.base;

import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;

import org.jooq.UpdatableRecord;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(ObjVRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> extnRecord) {
		super(repository, objRecord);
	}

}
