package dddrive.ddd.core.model

import dddrive.ddd.property.model.EntityWithProperties
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser

/**
 * A DDD Aggregate Root.
 */
interface Aggregate : EntityWithProperties {

	val id: Any

	val tenantId: Any

	val tenant: ObjTenant

	var owner: ObjUser?

	val caption: String

	val meta: AggregateMeta

}
