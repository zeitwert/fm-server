package io.zeitwert.fm.account.model

import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository

interface ObjAccountRepository : FMObjRepository<ObjAccount> {

	val contactRepository: ObjContactRepository

	val documentRepository: ObjDocumentRepository

}
