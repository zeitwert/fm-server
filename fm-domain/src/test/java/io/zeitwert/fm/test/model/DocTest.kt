package io.zeitwert.fm.test.model

import io.dddrive.doc.model.Doc
import io.dddrive.property.model.EnumSetProperty
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.test.model.enums.CodeTestType
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
