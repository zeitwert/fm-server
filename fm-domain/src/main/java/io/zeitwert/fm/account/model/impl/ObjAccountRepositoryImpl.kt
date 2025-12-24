package io.zeitwert.fm.account.model.impl

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.base.ObjAccountBase
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

@Component("objAccountRepository")
class ObjAccountRepositoryImpl(
	override val contactRepository: ObjContactRepository,
	override val documentRepository: ObjDocumentRepository,
) : FMObjRepositoryBase<ObjAccount>(
		ObjAccountRepository::class.java,
		ObjAccount::class.java,
		ObjAccountBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjAccountRepository {

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}

}
