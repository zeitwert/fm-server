package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.RepositoryDirectory
import io.crnk.core.engine.document.ErrorDataBuilder
import io.crnk.core.engine.http.HttpStatus
import io.crnk.core.exception.BadRequestException
import io.crnk.core.exception.ResourceNotFoundException
import io.crnk.core.queryspec.QuerySpec
import io.crnk.core.repository.ResourceRepositoryBase
import io.crnk.core.resource.list.DefaultResourceList
import io.crnk.core.resource.list.ResourceList
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateApiRepository
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.fm.oe.model.ObjUser
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * Generic API repository for aggregates using the GenericAggregateDtoAdapter.
 *
 * This replaces the need for custom repository implementations per aggregate type.
 * Each aggregate type only needs a concrete subclass that provides the adapter configuration.
 *
 * @param A The aggregate type
 * @param R The resource type (must extend GenericResourceBase)
 * @param resourceClass The class of the resource for crnk
 * @param adapter The configured adapter for this aggregate type
 * @param directory The repository directory
 * @param repository The aggregate repository
 * @param sessionCtx The session context
 */
abstract class AggregateApiRepositoryBase<A : Aggregate, R : AggregateDto<A>>(
	resourceClass: Class<R>,
	private val directory: RepositoryDirectory,
	private val repository: AggregateRepository<A>,
	private val adapter: AggregateDtoAdapterBase<A, R>,
	private val sessionCtx: SessionContext,
) : ResourceRepositoryBase<R, String>(resourceClass),
	AggregateApiRepository<A, R> {

	companion object {

		private val logger = LoggerFactory.getLogger(AggregateApiRepositoryBase::class.java)
	}

	@Transactional
	@Suppress("UNCHECKED_CAST")
	override fun <S : R> create(dto: S): S {
		if (dto.id != null) {
			throw BadRequestException("Cannot specify id on creation (${dto.id})")
		}
		try {
			logger.debug("create {} from {}", repository.intfClass.simpleName, dto)
			val aggregate = repository.create()
			logger.trace("create.created: {}", aggregate)
			toAggregate(dto, aggregate)
			logger.trace("create.assigned: {}", aggregate)
			repository.store(aggregate)
			logger.trace("create.stored: {}", aggregate)
			val dto = adapter.fromAggregate(aggregate) as S
			logger.trace("create.dto: {}", dto)
			return dto
		} catch (x: Exception) {
			throw RuntimeException("crashed on create", x)
		}
	}

	@Transactional
	override fun findOne(
		dtoId: String,
		querySpec: QuerySpec?,
	): R {
		try {
			val aggregate = repository.load(repository.idFromString(dtoId)!!)
			sessionCtx.addAggregate(aggregate.id, aggregate)
			return adapter.fromAggregate(aggregate)
		} catch (x: Exception) {
			x.printStackTrace()
			throw ResourceNotFoundException("${repository.aggregateType.defaultName}[$dtoId]")
		}
	}

	@Transactional
	override fun findAll(querySpec: QuerySpec): ResourceList<R> {
		try {
			val itemList = repository.find(querySpec)
			val list = DefaultResourceList<R>()
			list.addAll(itemList.map { adapter.fromAggregate(repository.get(it)) })
			return list
		} catch (x: Exception) {
			throw RuntimeException("crashed on findAll", x)
		}
	}

	@Transactional
	@Suppress("UNCHECKED_CAST")
	override fun <S : R> save(dto: S): S {
		logger.debug("save({}): {}", dto.javaClass.simpleName, dto)
		val id = repository.idFromString(dto.id) ?: throw BadRequestException("Can only save existing object (missing id)")
		dto.meta["clientVersion"] ?: throw BadRequestException("Missing meta.clientVersion")
		val clientVersion = dto.meta["clientVersion"] as Int
		val aggregate = sessionCtx.getAggregate(id) as A? ?: repository.load(id)

		if (clientVersion != aggregate.meta.version) {
			val modifiedByUserId = if (aggregate is Obj) {
				(aggregate as Obj).meta.modifiedByUserId
			} else {
				(aggregate as Doc).meta.modifiedByUserId
			}
			val userName = if (modifiedByUserId != null) {
				val userRepository = directory.getRepository(ObjUser::class.java)
				userRepository.get(modifiedByUserId).caption
			} else {
				"unknown"
			}
			throw BadRequestException(
				HttpStatus.CONFLICT_409,
				ErrorDataBuilder()
					.setStatus("${HttpStatus.CONFLICT_409}")
					.setTitle("Fehler beim Speichern")
					.setDetail(
						"Sie versuchten eine veraltete Version zu speichern." +
							" Benutzer $userName hat das Objekt in der Zwischenzeit bereits geändert." +
							" Ihre Änderungen wurden verworfen und die aktuelle Version geladen.",
					).build(),
			)
		}

		try {
			toAggregate(dto, aggregate)
			if (dto.hasOperation(AggregateApiRepository.CalculationOnlyOperation)) {
				return adapter.fromAggregate(aggregate) as S
			} else {
				repository.store(aggregate)
				return adapter.fromAggregate(repository.get(id)) as S
			}
		} catch (x: Exception) {
			throw RuntimeException("crashed on save", x)
		}

	}

	@Transactional
	@Suppress("UNCHECKED_CAST")
	override fun delete(dtoId: String) {
		try {
			if (repository !is ObjRepository<*>) {
				throw BadRequestException("Can only delete an Object")
			}
			val id = repository.idFromString(dtoId)!!
			val aggregate = sessionCtx.getAggregate(id) as A? ?: repository.load(id)
			(repository as ObjRepository<Obj>).close(aggregate as Obj)
			repository.store(aggregate)
		} catch (x: Exception) {
			throw RuntimeException("crashed on delete", x)
		}
	}

	/**
	 * Apply DTO values to aggregate.
	 * Override this method to add custom behavior (e.g., disabling calculations during update).
	 */
	protected open fun toAggregate(
		dto: R,
		aggregate: A,
	) {
		if (aggregate is Obj) {
			try {
				aggregate.meta.disableCalc()
				adapter.toAggregate(dto, aggregate)
			} finally {
				aggregate.meta.enableCalc()
				aggregate.meta.calcAll()
			}
		} else {
			adapter.toAggregate(dto, aggregate)
		}
	}

}
