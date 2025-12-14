package io.zeitwert.fm.test.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.UpdatableRecord
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

/** jOOQ-based persistence provider for ObjTest aggregates. */
@Component("objTestPersistenceProvider")
open class ObjTestPersistenceProvider : JooqObjPersistenceProviderBase<ObjTest>() {

	private lateinit var _dslContext: DSLContext

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun nextAggregateId(): Any = dslContext().nextval(Sequences.OBJ_ID_SEQ).toInt()

	override fun fromAggregate(aggregate: ObjTest): UpdatableRecord<*> = createObjRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: ObjTest) {
		val objId = aggregate.id as Int

		// Check if record exists
		val existingRecord =
			dslContext()
				.fetchOne(DSL.table("obj_test"), DSL.field("obj_id", Int::class.java).eq(objId))

		if (existingRecord != null) {
			// Update existing record
			dslContext()
				.update(DSL.table("obj_test"))
				.set(DSL.field("tenant_id", Int::class.java), aggregate.tenantId as? Int)
				.set(
					DSL.field("account_id", Int::class.java),
					(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value,
				).set(
					DSL.field("short_text", String::class.java),
					(aggregate.getProperty("shortText") as? BaseProperty<String?>)?.value,
				).set(
					DSL.field("long_text", String::class.java),
					(aggregate.getProperty("longText") as? BaseProperty<String?>)?.value,
				).set(
					DSL.field("date", LocalDate::class.java),
					(aggregate.getProperty("date") as? BaseProperty<LocalDate?>)?.value,
				).set(
					DSL.field("int", Int::class.java),
					(aggregate.getProperty("int") as? BaseProperty<Int?>)?.value,
				).set(
					DSL.field("is_done", Boolean::class.java),
					(aggregate.getProperty("isDone") as? BaseProperty<Boolean?>)?.value,
				).set(
					DSL.field("json", JSON::class.java),
					(aggregate.getProperty("json") as? BaseProperty<JSON?>)?.value,
				).set(
					DSL.field("nr", BigDecimal::class.java),
					(aggregate.getProperty("nr") as? BaseProperty<BigDecimal?>)?.value,
				).set(
					DSL.field("test_type_id", String::class.java),
					(aggregate.getProperty("testType") as? EnumProperty<CodeTestType>)?.value?.id,
				).set(
					DSL.field("ref_test_id", Int::class.java),
					(aggregate.getProperty("refTest") as? ReferenceProperty<ObjTest>)?.id as? Int,
				).where(DSL.field("obj_id", Int::class.java).eq(objId))
				.execute()
		} else {
			// Insert new record
			dslContext()
				.insertInto(DSL.table("obj_test"))
				.columns(
					DSL.field("obj_id", Int::class.java),
					DSL.field("tenant_id", Int::class.java),
					DSL.field("account_id", Int::class.java),
					DSL.field("short_text", String::class.java),
					DSL.field("long_text", String::class.java),
					DSL.field("date", LocalDate::class.java),
					DSL.field("int", Int::class.java),
					DSL.field("is_done", Boolean::class.java),
					DSL.field("json", JSON::class.java),
					DSL.field("nr", BigDecimal::class.java),
					DSL.field("test_type_id", String::class.java),
					DSL.field("ref_test_id", Int::class.java),
				).values(
					objId,
					aggregate.tenantId as? Int,
					(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value,
					(aggregate.getProperty("shortText") as? BaseProperty<String?>)?.value,
					(aggregate.getProperty("longText") as? BaseProperty<String?>)?.value,
					(aggregate.getProperty("date") as? BaseProperty<LocalDate?>)?.value,
					(aggregate.getProperty("int") as? BaseProperty<Int?>)?.value,
					(aggregate.getProperty("isDone") as? BaseProperty<Boolean?>)?.value,
					(aggregate.getProperty("json") as? BaseProperty<JSON?>)?.value,
					(aggregate.getProperty("nr") as? BaseProperty<BigDecimal?>)?.value,
					(aggregate.getProperty("testType") as? EnumProperty<CodeTestType>)?.value?.id,
					(aggregate.getProperty("refTest") as? ReferenceProperty<ObjTest>)?.id as? Int,
				).execute()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: ObjTest,
		objId: Int?,
	) {
		if (objId == null) return

		val record =
			dslContext()
				.select()
				.from(DSL.table("obj_test"))
				.where(DSL.field("obj_id", Int::class.java).eq(objId))
				.fetchOne()
				?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value =
			record.get("account_id", Int::class.java)
		(aggregate.getProperty("shortText") as? BaseProperty<String?>)?.value =
			record.get("short_text", String::class.java)
		(aggregate.getProperty("longText") as? BaseProperty<String?>)?.value =
			record.get("long_text", String::class.java)
		(aggregate.getProperty("date") as? BaseProperty<LocalDate?>)?.value =
			record.get("date", LocalDate::class.java)
		(aggregate.getProperty("int") as? BaseProperty<Int?>)?.value =
			record.get("int", Int::class.java)
		(aggregate.getProperty("isDone") as? BaseProperty<Boolean?>)?.value =
			record.get("is_done", Boolean::class.java)
		(aggregate.getProperty("json") as? BaseProperty<JSON?>)?.value =
			record.get("json", JSON::class.java)
		(aggregate.getProperty("nr") as? BaseProperty<BigDecimal?>)?.value =
			record.get("nr", BigDecimal::class.java)
		(aggregate.getProperty("refTest") as? ReferenceProperty<ObjTest>)?.id =
			record.get("ref_test_id", Int::class.java)

		// Load testType enum
		record.get("test_type_id", String::class.java)?.let { testTypeId ->
			(aggregate.getProperty("testType") as? EnumProperty<CodeTestType>)?.value =
				CodeTestType.getTestType(testTypeId)
		}
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_test"
	}

}
