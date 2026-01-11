package io.zeitwert.domain.test.model

import dddrive.app.doc.model.Doc
import dddrive.property.model.EnumSetProperty
import io.zeitwert.domain.test.model.enums.CodeTestType
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import java.math.BigDecimal
import java.time.LocalDate

interface DocTest :
	Doc,
	ItemWithNotes,
	ItemWithAccount {

	var shortText: String?

	var longText: String?

	var date: LocalDate?

	var int: Int?

	var isDone: Boolean?

	var json: String?

	var nr: BigDecimal?

	var testType: CodeTestType?

	var refObjId: Any?

	val refObj: ObjTest?

	var refDocId: Any?

	val refDoc: DocTest?

	val testTypeSet: EnumSetProperty<CodeTestType>

}
