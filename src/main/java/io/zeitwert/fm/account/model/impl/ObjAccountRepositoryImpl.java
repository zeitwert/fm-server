
package io.zeitwert.fm.account.model.impl;

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
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.ObjPartNoteRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objAccountRepository")
public class ObjAccountRepositoryImpl extends FMObjRepositoryBase<ObjAccount, ObjAccountVRecord>
		implements ObjAccountRepository {

	private static final String ITEM_TYPE = "obj_account";

	@Autowired
	//@formatter:off
	protected ObjAccountRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjPartNoteRepository noteRepository
	) {
		super(
			ObjAccountRepository.class,
			ObjAccount.class,
			ObjAccountBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
	}
	//@formatter:on

	@Override
	public ObjAccount doCreate(SessionInfo sessionInfo) {
		return doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_ACCOUNT));
	}

	@Override
	public void doInitParts(ObjAccount obj) {
		super.doInitParts(obj);
		this.getItemRepository().init(obj);
	}

	@Override
	public List<ObjAccountVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_ACCOUNT_V, Tables.OBJ_ACCOUNT_V.ID, querySpec);
	}

	@Override
	protected String getAccountIdField() {
		return "id";
	}

	@Override
	public ObjAccount doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjAccountRecord accountRecord = this.getDSLContext().fetchOne(Tables.OBJ_ACCOUNT,
				Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId));
		if (accountRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, accountRecord);
	}

	@Override
	public void doLoadParts(ObjAccount obj) {
		super.doLoadParts(obj);
		this.getItemRepository().load(obj);
		((ObjAccountBase) obj).loadAreaSet(this.getItemRepository().getPartList(obj, this.getAreaSetType()));
	}

	@Override
	public void doStoreParts(ObjAccount obj) {
		super.doStoreParts(obj);
		this.getItemRepository().store(obj);
	}

	@Override
	public Optional<ObjAccount> getByKey(SessionInfo sessionInfo, String key) {
		ObjAccountVRecord hhRecord = this.getDSLContext().fetchOne(Tables.OBJ_ACCOUNT_V,
				Tables.OBJ_ACCOUNT_V.INTL_KEY.eq(key));
		if (hhRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(sessionInfo, hhRecord.getId()));
	}

}
