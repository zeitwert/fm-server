package io.zeitwert.fm.test.model.impl

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.Property
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

open class ObjTestPartNodeImpl(
	obj: ObjTest,
	repository: PartRepository<ObjTest, ObjTestPartNode>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjTest>(obj, repository, property, id),
	ObjTestPartNode {

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

}
