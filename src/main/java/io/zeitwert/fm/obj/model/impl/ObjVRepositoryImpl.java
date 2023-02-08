
package io.zeitwert.fm.obj.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.jooq.repository.JooqObjRepositoryBase;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.ObjVBase;

@Component("objRepository")
public class ObjVRepositoryImpl extends JooqObjRepositoryBase<Obj, ObjRecord> implements ObjVRepository {

	private static final String AGGREGATE_TYPE = "obj";

	protected ObjVRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjVRepository.class, Obj.class, ObjVBase.class, AGGREGATE_TYPE, appContext, dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public Obj doCreate() {
		return this.doCreate(null);
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
