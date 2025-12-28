package dddrive.app.ddd.model

import dddrive.ddd.core.model.Aggregate

interface Aggregate : Aggregate {

	val tenantId: Any

	// val tenant: ObjTenant

	var ownerId: Any?

	// var owner: ObjUser?

	val caption: String

}
