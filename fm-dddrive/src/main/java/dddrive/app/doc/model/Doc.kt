package dddrive.app.doc.model

interface Doc : dddrive.ddd.core.model.Aggregate {

	override val meta: DocMeta

	val tenantId: Any

	// val tenant: ObjTenant

	var ownerId: Any?

	// var owner: ObjUser?

	val caption: String

	var assigneeId: Any?

	// var assignee: ObjUser?

}
