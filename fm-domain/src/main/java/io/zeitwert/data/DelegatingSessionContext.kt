package io.zeitwert.data

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.ObjUserRepository
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
	private val kernelContextProvider: ObjectProvider<KernelContext>,
) : SessionContext {

	companion object {

		private val setupMode = ThreadLocal.withInitial { false }
		private val localTenantId = ThreadLocal<Int?>()
		private val localUserId = ThreadLocal<Int?>()
		private val localAccountId = ThreadLocal<Int?>()

		fun isSetupMode(): Boolean = setupMode.get()

		fun startSetupMode() {
			setupMode.set(true)
		}

		fun stopSetupMode() {
			clearLocalContext()
			setupMode.set(false)
		}

		fun setTenantId(id: Int) {
			localTenantId.set(id)
			localUserId.set(null)
			localAccountId.set(null)
		}

		fun setUserId(id: Int) {
			localUserId.set(id)
		}

		fun setAccountId(id: Int?) {
			localAccountId.set(id)
		}

		fun clearLocalContext() {
			localTenantId.remove()
			localUserId.remove()
			localAccountId.remove()
		}
	}

	// Lazy-loaded kernel user ID for setup mode
	private val kernelUserId: Int by lazy {
		kernelContextProvider.getObject().kernelUserId as Int
	}

	// Lazy-loaded kernel tenant ID for setup mode
	private val kernelTenantId: Int by lazy {
		kernelContextProvider.getObject().kernelTenantId as Int
	}

	// Aggregates cache for setup mode
	private val setupAggregates: MutableMap<Any, Aggregate> = ConcurrentHashMap()

	override val tenantId: Any
		get() = if (isSetupMode()) localTenantId.get() ?: kernelTenantId else delegate.tenantId

	override val userId: Any
		get() = if (isSetupMode()) localUserId.get() ?: kernelUserId else delegate.userId

	override val accountId: Any?
		get() = if (isSetupMode()) localAccountId.get() else delegate.accountId

	override val locale: CodeLocale
		get() = if (isSetupMode()) CodeLocale.getLocale("en-US")!! else delegate.locale

	override val currentDate: LocalDate
		get() = LocalDate.now()

	override val currentTime: OffsetDateTime
		get() = OffsetDateTime.now()

	override fun hasAccount(): Boolean = if (isSetupMode()) localAccountId.get() != null else delegate.hasAccount()

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
