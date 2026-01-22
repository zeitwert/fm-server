package io.zeitwert.fm.account.model.impl

import io.zeitwert.app.obj.model.base.FMObjRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.persist.ObjAccountPersistenceProvider
import org.springframework.stereotype.Component
import java.util.*

@Component("objAccountRepository")
class ObjAccountRepositoryImpl(
	override val taskRepository: DocTaskRepository,
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjAccount>(
		ObjAccount::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjAccountRepository {

	override fun createAggregate(isNew: Boolean): ObjAccount = ObjAccountImpl(this, isNew)

	override fun getByKey(key: String): Optional<ObjAccount> {
		val accountId = (persistenceProvider as ObjAccountPersistenceProvider).getByKey(key)
		return accountId.map { id -> get(id) }
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}

}
