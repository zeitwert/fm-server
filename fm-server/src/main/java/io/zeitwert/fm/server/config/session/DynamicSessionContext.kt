package io.zeitwert.fm.server.config.session

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.enums.CodeLocale
import io.zeitwert.fm.server.config.security.AppUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * A SessionContext implementation that reads dynamically from SecurityContextHolder.
 *
 * This allows the context to reflect changes made during session reactivation
 * (e.g., switching accounts) without needing to recreate the bean.
 */
class DynamicSessionContext : SessionContext {

	private val aggregates: MutableMap<Any, Aggregate> = ConcurrentHashMap()

	private val userDetails: AppUserDetails
		get() = SecurityContextHolder.getContext().authentication.principal as AppUserDetails

	override val tenantId: Any
		get() = userDetails.tenantId

	override val userId: Any
		get() = userDetails.userId

	override val accountId: Any?
		get() = userDetails.accountId

	override val locale: CodeLocale = CodeLocale.DE_CH

	override fun hasAccount(): Boolean = accountId != null

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
