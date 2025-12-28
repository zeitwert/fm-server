package io.zeitwert.fm.account.model.impl

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.persist.ObjAccountSqlPersistenceProviderImpl
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component
import java.util.*

@Component("objAccountRepository")
class ObjAccountRepositoryImpl(
	override val requestCtx: RequestContextFM,
) : FMObjRepositoryBase<ObjAccount>(
		ObjAccount::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjAccountRepository {

	override fun createAggregate(isNew: Boolean): ObjAccount = ObjAccountImpl(this, isNew)

	override fun getByKey(key: String): Optional<ObjAccount> {
		val accountId = (persistenceProvider as ObjAccountSqlPersistenceProviderImpl).getByKey(key)
		return accountId.map { id -> get(id) }
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}

}
