package io.zeitwert.app.session.model.impl

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.enums.CodeLocale
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

data class SessionContextImpl(
	override val tenantId: Any,
	override val userId: Any,
	override var accountId: Any?,
	override val locale: CodeLocale,
) : SessionContext {

	private val aggregates: MutableMap<Any, Aggregate> = ConcurrentHashMap<Any, Aggregate>()

	override fun hasAccount(): Boolean = this.accountId != null

	override val currentDate: LocalDate get() = LocalDate.now()

	override val currentTime: OffsetDateTime get() = OffsetDateTime.now()

	override fun hasAggregate(id: Any): Boolean = aggregates.containsKey(id)

	override fun getAggregate(id: Any): Aggregate? = aggregates[id]

	override fun addAggregate(
		id: Any,
		aggregate: Aggregate,
	) {
		aggregates[id] = aggregate
	}

	override fun clearAggregates() {
		aggregates.clear()
	}

}
