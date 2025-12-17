package io.dddrive.core.ddd.model

import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.EntityWithProperties

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
