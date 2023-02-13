
package io.zeitwert.fm.obj.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.obj.model.base.ObjVBase;

@Component("objRepository")
public class ObjVRepositoryImpl extends FMObjRepositoryBase<Obj, ObjRecord> implements ObjVRepository {

	private static final String AGGREGATE_TYPE = "obj";

	protected ObjVRepositoryImpl() {
		super(ObjVRepository.class, Obj.class, ObjVBase.class, AGGREGATE_TYPE);
	}

	@Override
	public Obj doCreate() {
		assertThis(false, "not supported");
		return null;
	}

	@Override
	public Obj doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		return this.doLoad(objId, null);
	}

	@Override
	public List<ObjRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ, Tables.OBJ.ID, querySpec);
	}

}
