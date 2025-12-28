package dddrive.domain.ddd.model.impl

import dddrive.app.ddd.model.SessionContext
import java.time.OffsetDateTime

data class SessionContextImpl(
	override val tenantId: Any,
	override val accountId: Any,
	override val userId: Any,
) : SessionContext {

	override val timestamp get() = OffsetDateTime.now()

}
