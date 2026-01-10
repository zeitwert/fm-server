package io.zeitwert.dddrive.obj.api.jsonapi.base

import dddrive.app.obj.model.Obj
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoAdapterBase
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoBase
import io.zeitwert.dddrive.api.jsonapi.dto.EnumeratedDto

abstract class ObjDtoAdapterBase<O : Obj, D : ObjDtoBase>(
	aggregateClass: Class<O>,
	resourceType: String,
	dtoClass: Class<out AggregateDtoBase>,
	directory: RepositoryDirectory,
	resourceFactory: () -> D,
) : AggregateDtoAdapterBase<O, D>(aggregateClass, resourceType, dtoClass, directory, resourceFactory) {

	init {
		config.exclude("objTypeId")
		config.meta("itemType", {
			val itemType = CodeAggregateTypeEnum.getAggregateType((it as Obj).meta.objTypeId)
			EnumeratedDto.of(itemType)
		})
		config.meta(
			listOf(
				"closedByUser",
				"closedAt",
				"transitionList",
			),
		)
	}

}
