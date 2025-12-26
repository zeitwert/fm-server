package io.zeitwert.fm.account.model.impl

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

@Component("objAccountRepository")
class ObjAccountRepositoryImpl :
	FMObjRepositoryBase<ObjAccount>(
		ObjAccount::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjAccountRepository {

	override fun createAggregate(isNew: Boolean): ObjAccount = ObjAccountImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}

}
