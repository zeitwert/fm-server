package io.zeitwert.fm.building.model

import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository
import io.zeitwert.fm.task.model.DocTaskRepository

interface ObjBuildingRepository : FMObjRepository<ObjBuilding> {

	val accountRepository: ObjAccountRepository

	val contactRepository: ObjContactRepository

	val documentRepository: ObjDocumentRepository

	val taskRepository: DocTaskRepository

}
