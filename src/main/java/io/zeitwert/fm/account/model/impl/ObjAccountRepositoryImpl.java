
package io.zeitwert.fm.account.model.impl;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.base.ObjAccountBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

@Component("objAccountRepository")
public class ObjAccountRepositoryImpl extends ObjRepositoryBase<ObjAccount, ObjAccountVRecord>
		implements ObjAccountRepository {

	private static final String AGGREGATE_TYPE = "obj_account";

	protected ObjAccountRepositoryImpl(AppContext appContext) {
		super(ObjAccountRepository.class, ObjAccount.class, ObjAccountBase.class, AGGREGATE_TYPE, appContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(ObjRepository.getItemRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public List<ObjAccountVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_ACCOUNT_V, Tables.OBJ_ACCOUNT_V.ID, querySpec);
	}

}
