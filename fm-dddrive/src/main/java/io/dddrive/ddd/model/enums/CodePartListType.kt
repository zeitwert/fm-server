package io.dddrive.ddd.model.enums

import io.dddrive.enums.model.Enumerated

class CodePartListType(
	override val id: String,
	private val name: String,
) : Enumerated {

	override val enumeration: CodePartListTypeEnum
		get() = CodePartListTypeEnum.instance

	override fun getName(): String = name

}
