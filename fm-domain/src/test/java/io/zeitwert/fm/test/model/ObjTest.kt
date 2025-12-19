package io.zeitwert.fm.test.model

import io.dddrive.core.obj.model.Obj
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

	var refTestId: Int?

	val refTest: ObjTest?

	fun hasTestType(testType: CodeTestType?): Boolean

	val testTypeSet: MutableSet<CodeTestType?>?

	fun clearTestTypeSet()

	fun addTestType(testType: CodeTestType?)

	fun removeTestType(testType: CodeTestType?)

	val nodeCount: Int?

	fun getNode(seqNr: Int?): ObjTestPartNode?

	val nodeList: MutableList<ObjTestPartNode?>?

	fun getNodeById(nodeId: Int?): ObjTestPartNode?

	fun clearNodeList()

	fun addNode(): ObjTestPartNode?

	fun removeNode(nodeId: Int?)

}
