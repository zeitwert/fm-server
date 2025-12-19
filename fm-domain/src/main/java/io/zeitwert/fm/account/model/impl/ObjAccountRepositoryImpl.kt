package io.zeitwert.fm.account.model.impl

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.base.ObjAccountBase
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objAccountRepository")
class ObjAccountRepositoryImpl :
	FMObjRepositoryBase<ObjAccount>(
		ObjAccountRepository::class.java,
		ObjAccount::class.java,
		ObjAccountBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjAccountRepository {

	override lateinit var contactRepository: ObjContactRepository
	override lateinit var documentRepository: ObjDocumentRepository

	@Autowired
	@Lazy
	fun setContactRepository(contactRepository: ObjContactRepository) {
		this.contactRepository = contactRepository
	}

	@Autowired
	@Lazy
	fun setDocumentRepository(documentRepository: ObjDocumentRepository) {
		this.documentRepository = documentRepository
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}

}
