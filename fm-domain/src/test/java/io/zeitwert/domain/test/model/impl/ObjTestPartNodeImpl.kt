package io.zeitwert.domain.test.model.impl

import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.Property
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.ObjTestPartNode
import io.zeitwert.domain.test.model.enums.CodeTestType
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

}
