package io.zeitwert.config

import dddrive.ddd.core.model.Aggregate
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.enums.CodeLocale
import org.jooq.DSLContext
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * A delegating SessionContext that can switch between setup mode and normal mode.
 *
 * During data setup there's no web session, so the normal session-scoped SessionContext can't be created.
 * This delegating context:
 * - In data setup mode: can be initialized to return the desired tenant and user through the [TenantDSL]
 * - In normal mode: delegates to the session-scoped provider
 */
@Component("delegatingSessionContext")
@Primary
class DelegatingSessionContext(
	private val dslContextProvider: ObjectProvider<DSLContext>,
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : SessionContext {

	companion object {

		private val setupMode = ThreadLocal.withInitial { false }

		fun enterSetupMode() {
			setupMode.set(true)
		}

		fun exitSetupMode() {
			setupMode.set(false)
		}

		fun isSetupMode(): Boolean = setupMode.get()
	}

	// Lazy-loaded kernel user ID for setup mode
	private val kernelUserId: Int by lazy {
		dslContextProvider
			.getObject()
			.select(Tables.OBJ_USER.OBJ_ID)
			.from(Tables.OBJ_USER)
			.where(Tables.OBJ_USER.EMAIL.eq("k@zeitwert.io"))
			.fetchOne(Tables.OBJ_USER.OBJ_ID)!!
	}

	// Aggregates cache for setup mode
	private val setupAggregates: MutableMap<Any, Aggregate> = ConcurrentHashMap()

	override val tenantId: Any
		get() = if (isSetupMode()) ObjTenantRepository.KERNEL_TENANT_ID else delegate.tenantId

	override val userId: Any
		get() = if (isSetupMode()) kernelUserId else delegate.userId

	override val accountId: Any?
		get() = if (isSetupMode()) null else delegate.accountId

	override val locale: CodeLocale
		get() = if (isSetupMode()) CodeLocale.getLocale("en-US")!! else delegate.locale

	override val currentDate: LocalDate
		get() = LocalDate.now()

	override val currentTime: OffsetDateTime
		get() = OffsetDateTime.now()

	override fun hasAccount(): Boolean = if (isSetupMode()) false else delegate.hasAccount()

	override fun hasAggregate(id: Any): Boolean = if (isSetupMode()) setupAggregates.containsKey(id) else delegate.hasAggregate(id)

	override fun getAggregate(id: Any): Aggregate? = if (isSetupMode()) setupAggregates[id] else delegate.getAggregate(id)

	override fun addAggregate(
		id: Any,
		aggregate: Aggregate,
	) {
		if (isSetupMode()) {
			setupAggregates[id] = aggregate
		} else {
			delegate.addAggregate(id, aggregate)
		}
	}

	override fun clearAggregates() {
		if (isSetupMode()) {
			setupAggregates.clear()
		} else {
			delegate.clearAggregates()
		}
	}

	private val delegate: SessionContext
		get() = sessionContextProvider.getObject()

}
