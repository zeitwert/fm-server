package io.dddrive.core.ddd.model.enums

import io.dddrive.core.enums.model.base.EnumerationBase

class CodePartListTypeEnum : EnumerationBase<CodePartListType>(CodePartListType::class.java) {

	init {
		instance = this
	}

	companion object {

		lateinit var instance: CodePartListTypeEnum

		@JvmStatic
		fun getPartListType(partListTypeId: String): CodePartListType = instance.getItem(partListTypeId)

	}

}
