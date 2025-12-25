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

open class ObjTestImpl(
	repository: ObjTestRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTest,
	AggregateWithNotesMixin {

	// Simple base properties
	override var shortText: String? by baseProperty()
	override var longText: String? by baseProperty()
	override var date: LocalDate? by baseProperty()
	override var int: Int? by baseProperty()
	override var isDone: Boolean? by baseProperty()
	private var _json: JSON? by baseProperty()
	override var json: String?
		get() = _json?.data()
		set(value) {
			_json = if (value != null) JSON.json(value) else null
		}
	override var nr: BigDecimal? by baseProperty()

	// Enum property
	override var testType: CodeTestType? by enumProperty()

	// Reference properties
	override var refObjId: Any? by referenceIdProperty<ObjTest>()
	override var refObj: ObjTest? by referenceProperty()

	// Enum set property
	override val testTypeSet: EnumSetProperty<CodeTestType> by enumSetProperty()

	// Part list property
	override val nodeList: PartListProperty<ObjTestPartNode> by partListProperty()

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
