package io.zeitwert.fm.test.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.EnumSetProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.ObjTestRepository
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Base class for ObjTest using the NEW dddrive framework.
 */
abstract class ObjTestBase(
	repository: ObjTestRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTest,
	AggregateWithNotesMixin {

	// @formatter:off
	private val _shortText: BaseProperty<String> = this.addBaseProperty("shortText", String::class.java)
	private val _longText: BaseProperty<String> = this.addBaseProperty("longText", String::class.java)
	private val _date: BaseProperty<LocalDate> = this.addBaseProperty("date", LocalDate::class.java)
	private val _int: BaseProperty<Int> = this.addBaseProperty("int", Int::class.java)
	private val _isDone: BaseProperty<Boolean> = this.addBaseProperty("isDone", Boolean::class.java)
	private val _json: BaseProperty<JSON> = this.addBaseProperty("json", JSON::class.java)
	private val _nr: BaseProperty<BigDecimal> = this.addBaseProperty("nr", BigDecimal::class.java)
	private val _testType: EnumProperty<CodeTestType> = this.addEnumProperty("testType", CodeTestType::class.java)
	private val _refTest: ReferenceProperty<ObjTest> = this.addReferenceProperty("refTest", ObjTest::class.java)
	private val _testTypeSet: EnumSetProperty<CodeTestType> = this.addEnumSetProperty("testTypeSet", CodeTestType::class.java)
	private val _nodeList: PartListProperty<ObjTestPartNode> = this.addPartListProperty("nodeList", ObjTestPartNode::class.java)
	// @formatter:on

	override fun getRepository(): ObjTestRepository = super.getRepository() as ObjTestRepository

	override fun noteRepository(): ObjNoteRepository = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): ObjTest = this

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*>? {
		if (property === this._nodeList) {
			return directory.getPartRepository(ObjTestPartNode::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		val shortTextStr = getShortText() ?: ""
		val longTextStr = getLongText() ?: ""
		val refSuffix = if (getRefTestId() == null) "" else " (${getRefTest()?.getCaption() ?: ""})"
		this.caption.value = "[$shortTextStr, $longTextStr]$refSuffix"
	}

	// override fun getJson(): String? = _json.value?.toString()
	//
	// override fun setJson(json: String?) {
	// 	_json.value = if (json == null) null else JSON.valueOf(json)
	// }

	// // PartList operations
	// override fun getNodeCount(): Int = _nodeList.partCount ?: 0
	//
	// override fun getNode(seqNr: Int?): ObjTestPartNode? = _nodeList.getPart(seqNr)
	//
	// override fun getNodeList(): MutableList<ObjTestPartNode> = _nodeList.parts.toMutableList()
	//
	// override fun getNodeById(nodeId: Int?): ObjTestPartNode? = _nodeList.getPartById(nodeId)
	//
	// override fun clearNodeList() {
	// 	_nodeList.clearParts()
	// }
	//
	// override fun addNode(): ObjTestPartNode = _nodeList.addPart(null)
	//
	// override fun removeNode(nodeId: Int?) {
	// 	_nodeList.removePart(nodeId)
	// }

}
