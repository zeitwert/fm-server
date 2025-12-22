package io.zeitwert.fm.test.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.model.Property
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

abstract class ObjTestPartNodeBase(
	obj: ObjTest,
	repository: PartRepository<ObjTest, ObjTestPartNode>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjTest>(obj, repository, property, id),
	ObjTestPartNode {

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
	}

}
