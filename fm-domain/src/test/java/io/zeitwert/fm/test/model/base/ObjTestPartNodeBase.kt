package io.zeitwert.fm.test.model.base

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.Property
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

	private val _shortText = addBaseProperty("shortText", String::class.java)
	private val _longText = addBaseProperty("longText", String::class.java)
	private val _date = addBaseProperty("date", LocalDate::class.java)
	private val _int = addBaseProperty("int", Int::class.java)
	private val _isDone = addBaseProperty("isDone", Boolean::class.java)
	private val _json = addBaseProperty("json", JSON::class.java)
	private val _nr = addBaseProperty("nr", BigDecimal::class.java)
	private val _testType = addEnumProperty("testType", CodeTestType::class.java)
	private val _refObj = addReferenceProperty("refObj", ObjTest::class.java)

}
