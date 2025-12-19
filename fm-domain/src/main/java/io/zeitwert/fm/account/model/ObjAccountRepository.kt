package io.zeitwert.fm.account.model

import io.dddrive.core.obj.model.ObjRepository
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository

interface ObjAccountRepository : ObjRepository<ObjAccount> {

	val contactRepository: ObjContactRepository

	val documentRepository: ObjDocumentRepository

}
