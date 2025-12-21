package io.zeitwert.fm.test.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.PartListProperty
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

	private lateinit var _nodeList: PartListProperty<ObjTestPartNode>

	override fun doInit() {
		super.doInit()
		addBaseProperty("shortText", String::class.java)
		addBaseProperty("longText", String::class.java)
		addBaseProperty("date", LocalDate::class.java)
		addBaseProperty("int", Int::class.java)
		addBaseProperty("isDone", Boolean::class.java)
		addBaseProperty("json", JSON::class.java)
		addBaseProperty("nr", BigDecimal::class.java)
		addEnumProperty("testType", CodeTestType::class.java)
		addReferenceProperty("refObj", ObjTest::class.java)
		addEnumSetProperty("testTypeSet", CodeTestType::class.java)
		_nodeList = addPartListProperty("nodeList", ObjTestPartNode::class.java)
	}

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
		val refSuffix = if (refObjId == null) "" else " (${refObj!!.caption})"
		this.setCaption("[$shortTextStr, $longTextStr]$refSuffix")
	}

}
