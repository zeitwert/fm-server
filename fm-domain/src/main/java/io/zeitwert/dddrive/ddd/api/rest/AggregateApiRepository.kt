package io.zeitwert.dddrive.ddd.api.rest

import io.dddrive.ddd.model.Aggregate
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto

interface AggregateApiRepository<A : Aggregate, D : AggregateDto<A>> {

	fun getResourceClass(): Class<D>

	fun findOne(id: String): D

	fun findAll(): List<D>

	fun findAll(ids: Collection<String>): List<D>

	fun <S : D> save(dto: S): S

	fun <S : D> create(dto: S): S

	fun delete(id: String)

	companion object {

		const val CalculationOnlyOperation = "calculationOnly"
	}

}
