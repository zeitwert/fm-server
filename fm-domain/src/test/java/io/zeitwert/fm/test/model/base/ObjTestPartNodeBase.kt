package io.zeitwert.fm.test.model.base

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.JSON
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Base class for ObjTestPartNode using the NEW dddrive framework.
 */
abstract class ObjTestPartNodeBase(
	obj: ObjTest,
	repository: PartRepository<ObjTest, ObjTestPartNode>,
	property: Property<*>,
	id: Int?,
) : ObjPartBase<ObjTest>(obj, repository, property, id),
	ObjTestPartNode {

	// @formatter:off
	private val _shortText: BaseProperty<String> = this.addBaseProperty("shortText", String::class.java)
	private val _longText: BaseProperty<String> = this.addBaseProperty("longText", String::class.java)
	private val _date: BaseProperty<LocalDate> = this.addBaseProperty("date", LocalDate::class.java)
	private val _int: BaseProperty<Int> = this.addBaseProperty("int", Int::class.java)
	private val _isDone: BaseProperty<Boolean> = this.addBaseProperty("isDone", Boolean::class.java)
	private val _json: BaseProperty<JSON> = this.addBaseProperty("json", JSON::class.java)
	private val _nr: BaseProperty<BigDecimal> = this.addBaseProperty("nr", BigDecimal::class.java)
	private val _testType: EnumProperty<CodeTestType> = this.addEnumProperty("testType", CodeTestType::class.java)
	private val _refTest: ReferenceProperty<ObjTest> = this.addReferenceProperty("refTest", ObjTest::class.java)
	// @formatter:on

	// ObjTestPartNode interface implementation

	override fun getShortText(): String? = _shortText.value

	override fun setShortText(shortText: String?) {
		_shortText.value = shortText
	}

	override fun getLongText(): String? = _longText.value

	override fun setLongText(longText: String?) {
		_longText.value = longText
	}

	override fun getDate(): LocalDate? = _date.value

	override fun setDate(date: LocalDate?) {
		_date.value = date
	}

	override fun getInt(): Int? = _int.value

	override fun setInt(i: Int?) {
		_int.value = i
	}

	override fun getIsDone(): Boolean? = _isDone.value

	override fun setIsDone(isDone: Boolean?) {
		_isDone.value = isDone
	}

	override fun getJson(): String? = _json.value?.toString()

	override fun setJson(json: String?) {
		_json.value = if (json == null) null else JSON.valueOf(json)
	}

	override fun getNr(): BigDecimal? = _nr.value

	override fun setNr(nr: BigDecimal?) {
		_nr.value = nr
	}

	override fun getTestType(): CodeTestType? = _testType.value

	override fun setTestType(testType: CodeTestType?) {
		_testType.value = testType
	}

	override fun getRefTestId(): Int? = _refTest.id as? Int

	override fun setRefTestId(id: Int?) {
		_refTest.id = id
	}

	override fun getRefTest(): ObjTest? = _refTest.value

}
