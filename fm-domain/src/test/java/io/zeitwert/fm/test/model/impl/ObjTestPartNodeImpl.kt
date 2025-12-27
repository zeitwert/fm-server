package io.zeitwert.fm.test.model.impl

import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.Property
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

class ObjTestPartNodeImpl(
	obj: ObjTest,
	repository: PartRepository<ObjTest, ObjTestPartNode>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjTest>(obj, repository, property, id),
	ObjTestPartNode {

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

}
