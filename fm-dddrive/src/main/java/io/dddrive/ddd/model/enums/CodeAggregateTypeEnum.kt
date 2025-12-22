package io.dddrive.ddd.model.enums

import io.dddrive.enums.model.base.EnumerationBase

class CodeAggregateTypeEnum : EnumerationBase<CodeAggregateType>(CodeAggregateType::class.java) {

	init {
		instance = this
	}

	companion object {

		lateinit var instance: CodeAggregateTypeEnum

		@JvmStatic
		fun getAggregateType(aggregateTypeId: String): CodeAggregateType = instance.getItem(aggregateTypeId)

	}

}
