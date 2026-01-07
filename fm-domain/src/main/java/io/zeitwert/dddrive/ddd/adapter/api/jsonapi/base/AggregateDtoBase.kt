package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import dddrive.app.ddd.model.Aggregate
import io.crnk.core.resource.annotations.JsonApiId
import io.crnk.core.resource.annotations.JsonApiMetaInformation
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto

/**
 * Base class for generic JSON API resources.
 *
 * Uses @JsonAnyGetter/@JsonAnySetter for dynamic attribute handling,
 * allowing properties to be serialized without explicit field declarations.
 *
 * Subclasses only need to add the @JsonApiResource annotation with the
 * appropriate type and resourcePath.
 */
abstract class AggregateDtoBase<A : Aggregate> : AggregateDto<A> {

	@JsonApiMetaInformation
	override var meta: MutableMap<String, Any?>? = null

	@JsonIgnore
	private val attributes: MutableMap<String, Any?> = mutableMapOf()

	@JsonIgnore
	private val relations: MutableMap<String, Any?> = mutableMapOf()

	@JsonApiId
	override var id: String? = null
		get() = attributes["id"] as? String
		set(value) {
			attributes["id"] = value
			field = value
		}

	override fun containsKey(key: String): Boolean = attributes.containsKey(key)

	override operator fun get(key: String): Any? = attributes[key]

	@JsonAnyGetter
	fun any(): Map<String, Any?> = attributes

	@JsonAnySetter
	override operator fun set(
		key: String,
		value: Any?,
	) {
		attributes[key] = value
	}

	override fun hasRelation(name: String): Boolean = relations.containsKey(name)

	override fun setRelation(
		name: String,
		value: Any?,
	) {
		relations[name] = value
	}

	override fun getRelation(name: String): Any? = relations[name]

	@Suppress("UNCHECKED_CAST")
	override fun hasOperation(name: String): Boolean {
		val operations = meta?.get("operations") as List<String>? ?: emptyList()
		return operations.contains(name)
	}

	override fun toString() = "${javaClass.simpleName}[$id] meta: $meta $attributes"

}
