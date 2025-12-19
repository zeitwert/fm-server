package io.zeitwert.fm.test.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.Property
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

abstract class ObjTestBase(
	repository: ObjTestRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTest,
	AggregateWithNotesMixin {

	private val _shortText = addBaseProperty("shortText", String::class.java)
	private val _longText = addBaseProperty("longText", String::class.java)
	private val _date = addBaseProperty("date", LocalDate::class.java)
	private val _int = addBaseProperty("int", Int::class.java)
	private val _isDone = addBaseProperty("isDone", Boolean::class.java)
	private val _json = addBaseProperty("json", JSON::class.java)
	private val _nr = addBaseProperty("nr", BigDecimal::class.java)
	private val _testType = addEnumProperty("testType", CodeTestType::class.java)
	private val _refTest = addReferenceProperty("refTest", ObjTest::class.java)
	private val _testTypeSet = addEnumSetProperty("testTypeSet", CodeTestType::class.java)
	private val _nodeList = addPartListProperty("nodeList", ObjTestPartNode::class.java)

	override val repository get() = super.repository as ObjTestRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): ObjTest = this

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
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
		val shortTextStr = shortText ?: ""
		val longTextStr = longText ?: ""
		val refSuffix = if (refTestId == null) "" else " (${refTest!!.caption})"
		this._caption.value = "[$shortTextStr, $longTextStr]$refSuffix"
	}

}
