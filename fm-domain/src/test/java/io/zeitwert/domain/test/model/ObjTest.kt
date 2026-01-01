package io.zeitwert.domain.test.model

import dddrive.app.obj.model.Obj
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import io.zeitwert.domain.test.model.enums.CodeTestType
import io.zeitwert.fm.collaboration.model.ItemWithNotes
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

	val nodeList: PartListProperty<ObjTest, ObjTestPartNode>
}
