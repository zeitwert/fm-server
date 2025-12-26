package io.zeitwert.fm.test.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.enumSetProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
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

class ObjTestImpl(
	repository: ObjTestRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTest,
	AggregateWithNotesMixin {

	override var shortText: String? by baseProperty(this, "shortText")
	override var longText: String? by baseProperty(this, "longText")
	override var date: LocalDate? by baseProperty(this, "date")
	override var int: Int? by baseProperty(this, "integer")
	override var isDone: Boolean? by baseProperty(this, "isDone")
	private var _json: JSON? by baseProperty(this, "json")
	override var json: String?
		get() = _json?.data()
		set(value) {
			_json = if (value != null) JSON.json(value) else null
		}
	override var nr: BigDecimal? by baseProperty(this, "nr")
	override var testType: CodeTestType? by enumProperty(this, "testType")
	override var refObjId: Any? by referenceIdProperty<ObjTest>(this, "refObj")
	override var refObj: ObjTest? by referenceProperty(this, "refObj")
	override val testTypeSet: EnumSetProperty<CodeTestType> = enumSetProperty(this, "testTypeSet")
	override val nodeList: PartListProperty<ObjTestPartNode> = partListProperty(this, "nodeList")

	override val repository get() = super.repository as ObjTestRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): ObjTest = this

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === nodeList) {
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
