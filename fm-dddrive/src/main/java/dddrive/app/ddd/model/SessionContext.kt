package dddrive.app.ddd.model

import java.time.OffsetDateTime

interface SessionContext {

	val tenantId: Any

	val accountId: Any

	val userId: Any

	val timestamp: OffsetDateTime

}
