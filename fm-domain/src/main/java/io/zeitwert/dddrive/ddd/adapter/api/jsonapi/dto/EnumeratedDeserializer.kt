package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException

class EnumeratedDeserializer : StdDeserializer<EnumeratedDto> {

	constructor() : this(null)

	constructor(vc: Class<*>?) : super(vc)

	@Throws(IOException::class)
	override fun deserialize(
		jp: JsonParser,
		ctxt: DeserializationContext,
	): EnumeratedDto {
		val node: JsonNode = jp.codec.readTree(jp)
		val id = node.get("id").asText()
		val nameNode = node.get("name")
		val name = if (nameNode?.isNull != false) "" else nameNode.asText()
		return EnumeratedDto.of(id, name)
	}

}
