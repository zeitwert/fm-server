package io.zeitwert.config.session

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.enums.CodeLocale
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * An overridable SessionContext.
 *
 * During data setup there's no web session, so the normal session-scoped SessionContext can't be created.
 * This delegating context:
 * - In data setup mode: can be initialized to return the desired tenant and user through the [TenantDSL]
 * - In normal mode: delegates to the session-scoped provider
 */
@Component("testSessionContext")
@Primary
@Profile("!prod")
open class TestSessionContext(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
	private val kernelContextProvider: ObjectProvider<KernelContext>,
) : SessionContext {

	companion object {

		private val inOverride = ThreadLocal.withInitial { false }
		private val overrideTenantId = ThreadLocal<Int?>()
		private val overrideUserId = ThreadLocal<Int?>()
		private val overrideAccountId = ThreadLocal<Int?>()

		fun inOverride(): Boolean = inOverride.get()

		fun startOverride() {
			inOverride.set(true)
		}

		fun clearOverride() {
			require(inOverride()) { "must be in override mode" }
			overrideTenantId.remove()
			overrideUserId.remove()
			overrideAccountId.remove()
		}

		fun overrideTenantId(id: Int) {
			require(inOverride()) { "must be in override mode" }
			overrideTenantId.set(id)
			overrideUserId.set(null)
			overrideAccountId.set(null)
		}

		fun overrideUserId(id: Int) {
			require(inOverride()) { "must be in override mode" }
			overrideUserId.set(id)
		}

		fun overrideAccountId(id: Int?) {
			require(inOverride()) { "must be in override mode" }
			overrideAccountId.set(id)
		}

		fun stopOverride() {
			clearOverride()
			inOverride.set(false)
		}

	}

	// Lazy-loaded kernel tenant ID for setup mode
	private val kernelTenantId: Int by lazy {
		kernelContextProvider.getObject().kernelTenantId as Int
	}

	// Lazy-loaded kernel user ID for setup mode
	private val kernelUserId: Int by lazy {
		kernelContextProvider.getObject().kernelUserId as Int
	}

	override val tenantId: Any
		get() = if (inOverride()) overrideTenantId.get() ?: kernelTenantId else delegate.tenantId

	override val userId: Any
		get() = if (inOverride()) overrideUserId.get() ?: kernelUserId else delegate.userId

	override val accountId: Any?
		get() = if (inOverride()) overrideAccountId.get() else delegate.accountId

	override val locale: CodeLocale
		get() = if (inOverride()) CodeLocale.getLocale("en-US")!! else delegate.locale

	override val currentDate: LocalDate
		get() = if (inOverride()) LocalDate.now() else delegate.currentDate

	override val currentTime: OffsetDateTime
		get() = if (inOverride()) OffsetDateTime.now() else delegate.currentTime

	override fun hasAccount() = accountId != null

	override fun hasAggregate(id: Any) = delegate.hasAggregate(id)

	override fun getAggregate(id: Any) = delegate.getAggregate(id)

	override fun addAggregate(
		id: Any,
		aggregate: Aggregate,
	) = delegate.addAggregate(id, aggregate)

	override fun clearAggregates() = delegate.clearAggregates()

	private val delegate: SessionContext
		get() = sessionContextProvider.getObject()

}
