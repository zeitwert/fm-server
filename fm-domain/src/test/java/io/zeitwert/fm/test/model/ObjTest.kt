package io.zeitwert.fm.test.model

import io.dddrive.obj.model.Obj
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.test.model.enums.CodeTestType
import java.math.BigDecimal
import java.time.LocalDate

interface ObjTest :
	Obj,
	ItemWithNotes {

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

	val testTypeSet: EnumSetProperty<CodeTestType>

	val nodeList: PartListProperty<ObjTestPartNode>
}
