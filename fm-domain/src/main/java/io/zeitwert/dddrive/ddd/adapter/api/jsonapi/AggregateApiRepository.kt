package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

import dddrive.ddd.core.model.Aggregate
import io.crnk.core.queryspec.QuerySpec
import io.crnk.core.resource.list.ResourceList

interface AggregateApiRepository<A : Aggregate, D : AggregateDto<A>> {

	fun getResourceClass(): Class<D>

	fun findOne(
		dtoId: String,
		querySpec: QuerySpec? = null,
	): D

	fun findAll(querySpec: QuerySpec): ResourceList<D>

	fun <S : D> save(dto: S): S

	fun <S : D> create(dto: S): S

	fun delete(dtoId: String)

	companion object {

		const val CalculationOnlyOperation = "calculationOnly"
	}

}
