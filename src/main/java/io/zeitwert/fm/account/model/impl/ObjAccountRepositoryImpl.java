
package io.zeitwert.fm.account.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objAccountRepository")
public class ObjAccountRepositoryImpl extends FMObjRepositoryBase<ObjAccount, ObjAccountVRecord>
		implements ObjAccountRepository {

	private static final String AGGREGATE_TYPE = "obj_account";

	//@formatter:off
	protected ObjAccountRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			ObjAccountRepository.class,
			ObjAccount.class,
			ObjAccountBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public ObjAccount doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_ACCOUNT));
	}

	@Override
	public ObjAccount doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjAccountRecord accountRecord = this.getDSLContext().fetchOne(Tables.OBJ_ACCOUNT,
				Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId));
		if (accountRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, accountRecord);
	}

	@Override
	public Optional<ObjAccount> getByKey(String key) {
		ObjAccountVRecord accountRecord = this.getDSLContext().fetchOne(Tables.OBJ_ACCOUNT_V,
				Tables.OBJ_ACCOUNT_V.INTL_KEY.eq(key));
		if (accountRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(accountRecord.getId()));
	}

	@Override
	public List<ObjAccountVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_ACCOUNT_V, Tables.OBJ_ACCOUNT_V.ID, querySpec);
	}

}
