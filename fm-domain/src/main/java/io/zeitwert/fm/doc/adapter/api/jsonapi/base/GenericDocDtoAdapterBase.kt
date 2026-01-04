package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.DtoUtils
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocMetaDto
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.DocPartTransitionDto

abstract class GenericDocDtoAdapterBase<E : Doc, D : GenericDocDtoBase<E>>(
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : GenericAggregateDtoAdapterBase<E, D>(directory, resourceFactory) {

	init {
		relationship("tenantInfoId", "tenant", "tenant")
		relationship("accountId", "account", "account")
	}

	override fun fromAggregate(
		aggregate: E,
	): D {
		val dto = super.fromAggregate(aggregate)
		dto.meta = buildDocMeta(aggregate)
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
	private fun buildDocMeta(
		aggregate: Doc,
	): DocMetaDto {
		val meta = aggregate.meta
		val ownerId = aggregate.ownerId
		val modifiedByUserId = meta.modifiedByUserId
		val assigneeId = aggregate.assigneeId
		return DtoUtils.createDocMetaDto(
			EnumeratedDto.of(meta.repository.aggregateType),
			if (ownerId != null) EnumeratedDto.of(userRepository.get(ownerId)) else null,
			meta.version,
			EnumeratedDto.of(userRepository.get(meta.createdByUserId)),
			meta.createdAt,
			if (modifiedByUserId != null) EnumeratedDto.of(userRepository.get(modifiedByUserId)) else null,
			meta.modifiedAt,
			EnumeratedDto.of(meta.caseDef),
			EnumeratedDto.of(meta.caseStage),
			meta.caseStage?.isInWork ?: true,
			if (assigneeId != null) EnumeratedDto.of(userRepository.get(assigneeId)) else null,
			emptyList(),
			meta.transitionList.map { DocPartTransitionDto.fromPart(it, userRepository) },
		)
	}

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: E,
	) {
		// if (dto["caseStage"] != null) {
		// 	aggregate.meta.caseStage = userRepository.idFromString((dto["owner"] as Map<String, Any?>)["id"] as String)
		// 	aggregate.meta.setCaseStage(getCaseStage(dto["caseStage().id), null, null)
		// }
		if (dto["owner"] != null) {
			aggregate.ownerId = userRepository.idFromString((dto["owner"] as Map<String, Any?>)["id"] as String)
		}
		if (dto["assignee"] != null) {
			aggregate.assigneeId = userRepository.idFromString((dto["assignee"] as Map<String, Any?>)["id"] as String)
		}
		super.toAggregate(dto, aggregate)
	}

}
