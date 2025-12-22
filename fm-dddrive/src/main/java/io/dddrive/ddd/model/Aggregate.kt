package io.dddrive.ddd.model

import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.EntityWithProperties

/**
 * A DDD Aggregate Root.
 */
interface Aggregate : EntityWithProperties {

	val id: Any

	val tenantId: Any

	val tenant: ObjTenant

	var owner: ObjUser

	val caption: String

	val meta: AggregateMeta

}
