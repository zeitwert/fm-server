
package io.zeitwert.fm.obj.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.obj.model.db.Tables;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.obj.model.base.ObjVBase;

@Component("objRepository")
public class ObjVRepositoryImpl extends ObjRepositoryBase<Obj, ObjRecord> implements ObjVRepository {

	private static final String AGGREGATE_TYPE = "obj";

	protected ObjVRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				ObjVRepository.class,
				Obj.class,
				ObjVBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public Obj doCreate() {
		throw new RuntimeException("ObjV is readonly");
	}

	@Override
	public Obj doLoad(Integer objId) {
		ObjRecord objRecord = this.getDSLContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.newAggregate(objRecord, null);
	}

	@Override
	public List<ObjRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ, Tables.OBJ.ID, querySpec);
	}

}
