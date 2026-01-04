package dddrive.app.ddd.model

import dddrive.ddd.core.model.Aggregate

interface Aggregate : Aggregate {

	val tenantId: Any

	var ownerId: Any?

	val caption: String

	override val meta: AggregateMeta

}
