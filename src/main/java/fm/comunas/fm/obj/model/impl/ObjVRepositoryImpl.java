
package fm.comunas.fm.obj.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import fm.comunas.fm.obj.model.ObjVRepository;
import fm.comunas.fm.obj.model.base.ObjVBase;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartItemRepository;
import fm.comunas.ddd.obj.model.ObjPartTransitionRepository;
import fm.comunas.ddd.obj.model.base.ObjRepositoryBase;
import fm.comunas.ddd.obj.model.db.Tables;
import fm.comunas.ddd.obj.model.db.tables.records.ObjRecord;
import fm.comunas.ddd.session.model.SessionInfo;

@Component("objRepository")
public class ObjVRepositoryImpl extends ObjRepositoryBase<Obj, ObjRecord> implements ObjVRepository {

	private static final String ITEM_TYPE = "obj";

	@Autowired
	//@formatter:off
	protected ObjVRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository
	) {
		super(
			ObjVRepository.class,
			Obj.class,
			ObjVBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
	}
	//@formatter:on

	@Override
	public Optional<Obj> doLoad(SessionInfo sessionInfo, Integer objId) {
		ObjRecord objRecord = this.dslContext.fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.newAggregate(sessionInfo, objRecord, null));
	}

	@Override
	public List<ObjRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ, Tables.OBJ.ID, querySpec);
	}

	@Override
	public Obj doCreate(SessionInfo sessionInfo) {
		throw new RuntimeException("cannot create an Obj");
	}

}
