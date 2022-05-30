
package io.zeitwert.ddd.oe.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.base.ObjUserBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserRecord;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.session.service.api.SessionService;

@Component("objUserRepository")
public class ObjUserRepositoryImpl extends ObjRepositoryBase<ObjUser, ObjUserVRecord> implements ObjUserRepository {

	private static final String ITEM_TYPE = "obj_user";

	private final SessionInfo globalSessionInfo;

	@Autowired
	//@formatter:off
	protected ObjUserRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final SessionService sessionService
	) {
		super(
			ObjUserRepository.class,
			ObjUser.class,
			ObjUserBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
		this.globalSessionInfo = sessionService.getGlobalSession();
	}
	//@formatter:on

	@Override
	public ObjUser doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjRecord objRecord = this.getDSLContext().fetchOne(io.zeitwert.ddd.obj.model.db.Tables.OBJ,
				io.zeitwert.ddd.obj.model.db.Tables.OBJ.ID.eq(objId));
		ObjUserRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER, Tables.OBJ_USER.OBJ_ID.eq(objId));
		if (objRecord == null || userRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return newAggregate(sessionInfo, objRecord, userRecord);
	}

	@Override
	public List<ObjUserVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_USER_V, Tables.OBJ_USER_V.ID, querySpec);
	}

	@Override
	public ObjUser get(Integer id) {
		return this.get(this.globalSessionInfo, id);
	}

	@Override
	public Optional<ObjUser> getByEmail(String email) {
		ObjUserVRecord userRecord = this.getDSLContext().fetchOne(Tables.OBJ_USER_V, Tables.OBJ_USER_V.EMAIL.eq(email));
		if (userRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(this.globalSessionInfo, userRecord.getId()));
	}

	@Override
	public ObjUser doCreate(SessionInfo sessionInfo) {
		throw new RuntimeException("cannot create a User");
	}

}
