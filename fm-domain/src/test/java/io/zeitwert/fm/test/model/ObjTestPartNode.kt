package io.zeitwert.fm.test.model

import io.dddrive.core.obj.model.ObjPart
import io.zeitwert.fm.test.model.enums.CodeTestType
import java.math.BigDecimal
import java.time.LocalDate

interface ObjTestPartNode : ObjPart<ObjTest> {

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

}
