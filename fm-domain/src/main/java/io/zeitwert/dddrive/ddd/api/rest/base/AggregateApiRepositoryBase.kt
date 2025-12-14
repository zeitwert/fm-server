package io.zeitwert.dddrive.ddd.api.rest.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.dddrive.ddd.api.rest.AggregateApiRepository
import io.zeitwert.dddrive.ddd.api.rest.AggregateApiRepository.Companion.CalculationOnlyOperation
import io.zeitwert.dddrive.ddd.api.rest.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.api.rest.DtoDetailLevel
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto
import io.zeitwert.fm.app.model.RequestContextFM
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime

abstract class AggregateApiRepositoryBase<A : Aggregate, D : AggregateDto<A>>(
	private val dtoClass: Class<D>,
	private val requestContext: RequestContextFM,
	private val userRepo: ObjRepository<ObjUser>,
	private val repository: AggregateRepository<A>,
	private val dtoAdapter: AggregateDtoAdapter<A, D>,
) : AggregateApiRepository<A, D> {

	override fun getResourceClass(): Class<D> = dtoClass

	protected fun getUserRepo(): ObjRepository<ObjUser> = userRepo

	@Suppress("UNCHECKED_CAST")
	override fun <S : D> create(dto: S): S {
		if (dto.getId() != null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot specify id on creation (${dto.getId()})")
		}
		try {
			val tenantId = if (dto.getTenant() != null) {
				repository.idFromString(dto.getTenant()!!.id!!)
			} else {
				requestContext.tenantId
			}
			val aggregate = repository.create(tenantId, requestContext.userId, OffsetDateTime.now())
			dtoAdapter.toAggregate(dto, aggregate)
			repository.store(aggregate, requestContext.userId, OffsetDateTime.now())
			return dtoAdapter.fromAggregate(aggregate, DtoDetailLevel.FULL) as S
		} catch (x: Exception) {
			throw RuntimeException("crashed on create", x)
		}
	}

	override fun findOne(objId: String): D {
		try {
			val aggregate = repository.load(repository.idFromString(objId))
			requestContext.clearAggregates()
			requestContext.addAggregate(aggregate.id, aggregate)
			return dtoAdapter.fromAggregate(aggregate, DtoDetailLevel.FULL)
		} catch (x: Exception) {
			x.printStackTrace()
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "${repository.aggregateType.name}[$objId]")
		}
	}

	override fun findAll(): List<D> {
		return emptyList()
		// try {
		// 	val itemList = repository.getAll(requestContext.tenantId)
		// 	return itemList.map { item -> dtoAdapter.fromAggregate(item, DtoDetailLevel.REPORT) }
		// } catch (x: Exception) {
		// 	throw RuntimeException("crashed on findAll", x)
		// }
	}

	override fun findAll(ids: Collection<String>): List<D> = findAll().filter { item -> ids.contains(item.getId()) }

	@Suppress("UNCHECKED_CAST")
	override fun <S : D> save(dto: S): S {
		val id = repository.idFromString(dto.getId())
		if (id == null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only save existing object (missing id)")
		} else if (dto.getMeta() == null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing meta information (version or operation)")
		} else if (dto.getMeta()!!.getClientVersion() == null && !dto.getMeta()!!.hasOperation(CalculationOnlyOperation)) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing meta information (version or operation)")
		}

		try {
			val aggregate = if (requestContext.hasAggregate(id)) {
				requestContext.getAggregate(id) as A
			} else {
				repository.load(id)
			}

			if (dto.getMeta()!!.hasOperation(CalculationOnlyOperation)) {
				dtoAdapter.toAggregate(dto, aggregate)
			} else {
				val clientVersion = dto.getMeta()!!.getClientVersion()
					?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing clientVersion")

				if (clientVersion != aggregate.meta.version) {
					throw ResponseStatusException(HttpStatus.CONFLICT, "Version conflict: trying to store an outdated version")
				}

				dtoAdapter.toAggregate(dto, aggregate)
				repository.store(aggregate, requestContext.userId, OffsetDateTime.now())
				return dtoAdapter.fromAggregate(repository.get(id), DtoDetailLevel.FULL) as S
			}

			return dtoAdapter.fromAggregate(aggregate, DtoDetailLevel.FULL) as S
		} catch (x: Exception) {
			throw RuntimeException("crashed on save", x)
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun delete(uiId: String) {
		val id = repository.idFromString(uiId)
		if (id == null) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Can only delete existing object (missing id)")
		}

		try {
			val aggregate = if (requestContext.hasAggregate(id)) {
				requestContext.getAggregate(id) as A
			} else {
				repository.load(id)
			}

			if (aggregate !is Obj) {
				throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete an Object")
			}

			aggregate.delete(requestContext.userId, OffsetDateTime.now())
			repository.store(aggregate, requestContext.userId, OffsetDateTime.now())
		} catch (x: Exception) {
			throw RuntimeException("crashed on delete", x)
		}
	}

}
