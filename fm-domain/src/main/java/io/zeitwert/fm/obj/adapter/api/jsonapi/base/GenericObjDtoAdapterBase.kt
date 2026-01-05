package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto

abstract class GenericObjDtoAdapterBase<O : Obj, D : GenericObjDtoBase<O>>(
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : GenericAggregateDtoAdapterBase<O, D>(directory, resourceFactory) {

	init {
		exclude("objTypeId")
		meta("itemType", {
			val itemType = CodeAggregateTypeEnum.getAggregateType((it as Obj).meta.objTypeId)
			EnumeratedDto.of(itemType)
		})
		meta(
			listOf(
				"closedByUser",
				"closedAt",
				"transitionList",
			),
		)
		relationship("tenantInfoId", "tenant", "tenant")
		relationship("accountId", "account", "account")
	}

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: D,
		aggregate: O,
	) {
		if (dto["owner"] != null) {
			aggregate.ownerId = userRepository.idFromString(dto.enumId("owner"))
		}
		super.toAggregate(dto, aggregate)
	}

}
