
package io.zeitwert.fm.account.model.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objAccountRepository")
public class ObjAccountRepositoryImpl extends FMObjRepositoryBase<ObjAccount, ObjAccountVRecord>
		implements ObjAccountRepository {

	private static final String AGGREGATE_TYPE = "obj_account";

	protected ObjAccountRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjAccountRepository.class, ObjAccount.class, ObjAccountBase.class, AGGREGATE_TYPE, appContext, dslContext);
	}

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
