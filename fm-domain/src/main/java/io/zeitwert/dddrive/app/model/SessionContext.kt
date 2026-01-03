package io.zeitwert.dddrive.app.model

import dddrive.app.ddd.model.SessionContext
import dddrive.ddd.core.model.Aggregate
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.enums.CodeLocale
import java.time.LocalDate
import java.time.OffsetDateTime

interface SessionContext : SessionContext {

	override val tenantId: Any

	fun hasAccount(): Boolean

	override val accountId: Any?

	override val userId: Any

	val user: ObjUser

	val locale: CodeLocale

	fun hasAggregate(id: Any): Boolean

	fun getAggregate(id: Any): Aggregate?

	fun addAggregate(
		id: Any,
		aggregate: Aggregate,
	)

	fun clearAggregates()

	val currentDate: LocalDate

	override val currentTime: OffsetDateTime

}
