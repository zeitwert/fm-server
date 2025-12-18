package io.dddrive.dddrive.ddd.persist.mem.pto

open class AggregatePto(
	var id: Int? = null,
	var tenantId: Int? = null,
	initialMeta: AggregateMetaPto? = null,
	var caption: String? = null,
) {

	open val meta: AggregateMetaPto? = initialMeta

}
