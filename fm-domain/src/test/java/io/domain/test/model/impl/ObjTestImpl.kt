package io.domain.test.model.impl

import dddrive.ddd.model.Part
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.enumSetProperty
import dddrive.property.delegate.partListProperty
import dddrive.property.delegate.partReferenceProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import dddrive.property.model.Property
import io.domain.test.model.ObjTest
import io.domain.test.model.ObjTestPartNode
import io.domain.test.model.ObjTestRepository
import io.domain.test.model.enums.CodeTestType
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

class ObjTestImpl(
	repository: ObjTestRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTest,
	AggregateWithNotesMixin {

	override var shortText by baseProperty<String>("shortText")
	override var longText by baseProperty<String>("longText")
	override var date by baseProperty<LocalDate>("date")
	override var int by baseProperty<Int>("integer")
	override var isDone by baseProperty<Boolean>("isDone")
	private var _json by baseProperty<JSON>("json")
	override var json: String?
		get() = _json?.data()
		set(value) {
			_json = if (value != null) JSON.json(value) else null
		}
	override var nr by baseProperty<BigDecimal>("nr")
	override var testType by enumProperty<CodeTestType>("testType")
	override var refObjId by referenceIdProperty<ObjTest>("refObj")
	override var refObj by referenceProperty<ObjTest>("refObj")
	override val testTypeSet = enumSetProperty<CodeTestType>("testTypeSet")
	override val nodeList = partListProperty<ObjTest, ObjTestPartNode>("nodeList")

	override var firstNode by partReferenceProperty<ObjTest, ObjTestPartNode>("firstNode") { _ ->
		nodeList.firstOrNull()?.id
	}

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
