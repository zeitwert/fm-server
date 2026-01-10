package io.zeitwert.app.api.jsonapi

import io.crnk.core.queryspec.QuerySpec
import io.crnk.core.resource.list.ResourceList

interface ResourceRepository<D : ResourceDto> {

	fun getResourceClass(): Class<D>

	fun findOne(
		dtoId: String,
		querySpec: QuerySpec? = null,
	): D

	fun findAll(querySpec: QuerySpec): ResourceList<D>

	fun <S : D> create(dto: S): S

	fun <S : D> save(dto: S): S

	fun delete(dtoId: String)

	companion object {

		const val CalculationOnlyOperation = "calculationOnly"

	}

}
