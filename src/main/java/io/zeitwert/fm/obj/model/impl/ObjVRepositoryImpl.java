
package io.zeitwert.fm.obj.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.jooq.obj.JooqObjRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.ObjPersistenceProviderMixin;
import io.zeitwert.fm.obj.model.base.ObjVBase;

@Component("objRepository")
public class ObjVRepositoryImpl extends JooqObjRepositoryBase<Obj, ObjRecord>
		implements ObjVRepository, ObjPersistenceProviderMixin<Obj>, AggregateFindMixin<ObjRecord> {

	private static final String AGGREGATE_TYPE = "obj";

	protected ObjVRepositoryImpl() {
		super(ObjVRepository.class, Obj.class, ObjVBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("accountId", AggregateState.BASE, "account_id", Integer.class);
	}

	@Override
	public boolean hasAccount() {
		return true;
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

	@Override
	public final List<ObjRecord> find(QuerySpec querySpec) {
		return this.doFind(this.queryWithFilter(querySpec, (RequestContextFM) this.getAppContext().getRequestContext()));
	}

}
