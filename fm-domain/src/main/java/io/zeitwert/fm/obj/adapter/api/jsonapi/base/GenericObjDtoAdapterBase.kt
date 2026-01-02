package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericDtoHelper
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjMetaDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto

abstract class GenericObjDtoAdapterBase<O : Obj, D : GenericObjDtoBase<O>>(
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : GenericAggregateDtoAdapterBase<O, D>(directory, resourceFactory) {

	init {
		relationship("tenantInfoId", "tenant", "tenantId")
		relationship("accountId", "account", "accountId")
	}

	override fun fromAggregate(
		aggregate: O,
	): D {
		val dto = super.fromAggregate(aggregate)
		dto["id"] = aggregate.id.toString()
		dto.meta = buildObjMeta(aggregate)
		dto["tenant"] = EnumeratedDto.of(tenantRepository.get(aggregate.tenantId))
		val ownerId = aggregate.ownerId
		dto["owner"] = if (ownerId != null) {
			EnumeratedDto.of(userRepository.get(ownerId))
		} else {
			null
		}
		return dto
	}

	/**
	 * Build meta information for the aggregate.
	 */
	private fun buildObjMeta(
		aggregate: Obj,
	): ObjMetaDto {
		val meta = aggregate.meta
		val ownerId = aggregate.ownerId
		val modifiedByUserId = meta.modifiedByUserId
		val closedByUserId = meta.closedByUserId
		return GenericDtoHelper.createObjMetaDto(
			EnumeratedDto.of(meta.repository.aggregateType),
			if (ownerId != null) EnumeratedDto.of(userRepository.get(ownerId)) else null,
			meta.version,
			EnumeratedDto.of(userRepository.get(meta.createdByUserId)),
			meta.createdAt,
			if (modifiedByUserId != null) EnumeratedDto.of(userRepository.get(modifiedByUserId)) else null,
			meta.modifiedAt,
			if (closedByUserId != null) EnumeratedDto.of(userRepository.get(closedByUserId)) else null,
			meta.closedAt,
			meta.transitionList.map { ObjPartTransitionDto.fromPart(it, userRepository) },
		)
	}

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: O,
	) {
		if (dto["owner"] != null) {
			aggregate.ownerId = userRepository.idFromString((dto["owner"] as Map<String, Any?>)["id"] as String)
		}
		super.toAggregate(dto, aggregate)
	}

}
