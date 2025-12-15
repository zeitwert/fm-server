package io.zeitwert.fm.test.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.persist.JooqDocPersistenceProviderBase
import io.zeitwert.fm.doc.model.db.Sequences
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.DocTestRepository
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.UpdatableRecord
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

/**
 * jOOQ-based persistence provider for DocTest aggregates.
 */
@Component("docTestPersistenceProvider")
open class DocTestPersistenceProvider : JooqDocPersistenceProviderBase<DocTest>() {

	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: DocTestRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: DocTestRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun getDefaultCaseDefId(): String = "test"

	override fun getDefaultCaseStageId(): String = "test.new"

	override fun nextAggregateId(): Any =
		dslContext()
			.nextval(Sequences.DOC_ID_SEQ)
			.toInt()

	override fun fromAggregate(aggregate: DocTest): UpdatableRecord<*> = createDocRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: DocTest) {
		val docId = aggregate.id as Int

		// Check if record exists
		val existingRecord = dslContext().fetchOne(
			DSL.table("doc_test"),
			DSL.field("doc_id", Int::class.java).eq(docId),
		)

		if (existingRecord != null) {
			// Update existing record
			dslContext()
				.update(DSL.table("doc_test"))
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
				).set(DSL.field("int", Int::class.java), (aggregate.getProperty("int") as? BaseProperty<Int?>)?.value)
				.set(
					DSL.field("is_done", Boolean::class.java),
					(aggregate.getProperty("isDone") as? BaseProperty<Boolean?>)?.value,
				).set(DSL.field("json", JSON::class.java), (aggregate.getProperty("json") as? BaseProperty<JSON?>)?.value)
				.set(
					DSL.field("nr", BigDecimal::class.java),
					(aggregate.getProperty("nr") as? BaseProperty<BigDecimal?>)?.value,
				).set(
					DSL.field("test_type_id", String::class.java),
					(aggregate.getProperty("testType") as? EnumProperty<CodeTestType>)?.value?.id,
				).set(
					DSL.field("ref_obj_id", Int::class.java),
					(aggregate.getProperty("refObj") as? ReferenceProperty<ObjTest>)?.id as? Int,
				).set(
					DSL.field("ref_doc_id", Int::class.java),
					(aggregate.getProperty("refDoc") as? ReferenceProperty<DocTest>)?.id as? Int,
				).where(DSL.field("doc_id", Int::class.java).eq(docId))
				.execute()
		} else {
			// Insert new record
			dslContext()
				.insertInto(DSL.table("doc_test"))
				.columns(
					DSL.field("doc_id", Int::class.java),
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
					DSL.field("ref_obj_id", Int::class.java),
					DSL.field("ref_doc_id", Int::class.java),
				).values(
					docId,
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
					(aggregate.getProperty("refObj") as? ReferenceProperty<ObjTest>)?.id as? Int,
					(aggregate.getProperty("refDoc") as? ReferenceProperty<DocTest>)?.id as? Int,
				).execute()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: DocTest,
		docId: Int?,
	) {
		if (docId == null) return

		val record = dslContext()
			.select()
			.from(DSL.table("doc_test"))
			.where(DSL.field("doc_id", Int::class.java).eq(docId))
			.fetchOne() ?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.get("account_id", Int::class.java)
		(aggregate.getProperty("shortText") as? BaseProperty<String?>)?.value = record.get("short_text", String::class.java)
		(aggregate.getProperty("longText") as? BaseProperty<String?>)?.value = record.get("long_text", String::class.java)
		(aggregate.getProperty("date") as? BaseProperty<LocalDate?>)?.value = record.get("date", LocalDate::class.java)
		(aggregate.getProperty("int") as? BaseProperty<Int?>)?.value = record.get("int", Int::class.java)
		(aggregate.getProperty("isDone") as? BaseProperty<Boolean?>)?.value = record.get("is_done", Boolean::class.java)
		(aggregate.getProperty("json") as? BaseProperty<JSON?>)?.value = record.get("json", JSON::class.java)
		(aggregate.getProperty("nr") as? BaseProperty<BigDecimal?>)?.value = record.get("nr", BigDecimal::class.java)
		(aggregate.getProperty("refObj") as? ReferenceProperty<ObjTest>)?.id = record.get("ref_obj_id", Int::class.java)
		(aggregate.getProperty("refDoc") as? ReferenceProperty<DocTest>)?.id = record.get("ref_doc_id", Int::class.java)

		// Load testType enum
		record.get("test_type_id", String::class.java)?.let { testTypeId ->
			(aggregate.getProperty("testType") as? EnumProperty<CodeTestType>)?.value =
				CodeTestType.getTestType(testTypeId)
		}
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_test"
	}

}
