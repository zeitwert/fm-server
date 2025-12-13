package io.zeitwert.fm.test.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.EnumSetProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
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
	repository: ObjTestRepository
) : FMObjBase(repository), ObjTest {

	//@formatter:off
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
	//@formatter:on

	override fun getRepository(): ObjTestRepository = super.getRepository() as ObjTestRepository

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

	// ObjTest interface implementation

	override fun getShortText(): String? = _shortText.value

	override fun setShortText(shortText: String?) {
		_shortText.value = shortText
	}

	override fun getLongText(): String? = _longText.value

	override fun setLongText(longText: String?) {
		_longText.value = longText
	}

	override fun getDate(): LocalDate? = _date.value

	override fun setDate(date: LocalDate?) {
		_date.value = date
	}

	override fun getInt(): Int? = _int.value

	override fun setInt(i: Int?) {
		_int.value = i
	}

	override fun getIsDone(): Boolean? = _isDone.value

	override fun setIsDone(isDone: Boolean?) {
		_isDone.value = isDone
	}

	override fun getJson(): String? = _json.value?.toString()

	override fun setJson(json: String?) {
		_json.value = if (json == null) null else JSON.valueOf(json)
	}

	override fun getNr(): BigDecimal? = _nr.value

	override fun setNr(nr: BigDecimal?) {
		_nr.value = nr
	}

	override fun getTestType(): CodeTestType? = _testType.value

	override fun setTestType(testType: CodeTestType?) {
		_testType.value = testType
	}

	fun getTestTypeId(): String? = _testType.value?.id

	override fun getRefTestId(): Int? = _refTest.id as? Int

	override fun setRefTestId(id: Int?) {
		_refTest.id = id
	}

	override fun getRefTest(): ObjTest? = _refTest.value

	// EnumSet operations
	override fun hasTestType(testType: CodeTestType): Boolean = _testTypeSet.hasItem(testType)

	override fun getTestTypeSet(): MutableSet<CodeTestType> = _testTypeSet.items.toMutableSet()

	override fun clearTestTypeSet() {
		_testTypeSet.clearItems()
	}

	override fun addTestType(testType: CodeTestType) {
		_testTypeSet.addItem(testType)
	}

	override fun removeTestType(testType: CodeTestType) {
		_testTypeSet.removeItem(testType)
	}

	// PartList operations
	override fun getNodeCount(): Int = _nodeList.partCount ?: 0

	override fun getNode(seqNr: Int?): ObjTestPartNode? = _nodeList.getPart(seqNr)

	override fun getNodeList(): MutableList<ObjTestPartNode> = _nodeList.parts.toMutableList()

	override fun getNodeById(nodeId: Int?): ObjTestPartNode? = _nodeList.getPartById(nodeId)

	override fun clearNodeList() {
		_nodeList.clearParts()
	}

	override fun addNode(): ObjTestPartNode = _nodeList.addPart(null)

	override fun removeNode(nodeId: Int?) {
		_nodeList.removePart(nodeId)
	}

	// Note operations (implemented directly, bypassing mixin)
	private fun noteRepository(): ObjNoteRepository =
		(getRepository() as io.zeitwert.fm.test.model.impl.ObjTestRepositoryImpl).getNoteRepository()

	override fun getNotes(): List<ObjNote> {
		return noteRepository().getByForeignKey("related_to_id", this.id)
	}

	override fun addNote(noteType: CodeNoteType): ObjNote {
		val note = noteRepository().create(this.tenantId, null, null)
		note.noteType = noteType
		note.relatedToId = this.id
		return note
	}

	override fun removeNote(noteId: Any) {
		val note = noteRepository().load(noteId)
		check(this.id == note.relatedToId) { "Note is not related to this item." }
		noteRepository().delete(note, null, null)
	}

}
