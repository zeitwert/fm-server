package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import dddrive.app.ddd.model.Aggregate
import io.crnk.core.resource.annotations.JsonApiId
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto

/**
 * Base class for generic JSON API resources.
 *
 * Uses @JsonAnyGetter/@JsonAnySetter for dynamic attribute handling,
 * allowing properties to be serialized without explicit field declarations.
 *
 * Subclasses only need to add the @JsonApiResource annotation with the
 * appropriate type and resourcePath.
 */
abstract class GenericAggregateDtoBase<A : Aggregate> : GenericAggregateDto<A> {

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

	override fun hasAttribute(name: String): Boolean = attributes.containsKey(name)

	override operator fun get(name: String): Any? = attributes[name]

	@JsonAnyGetter
	fun any(): Map<String, Any?> = attributes

	@JsonAnySetter
	override operator fun set(
		name: String,
		value: Any?,
	) {
		attributes[name] = value
	}

	override fun hasRelation(name: String): Boolean = relations.containsKey(name)

	override fun setRelation(
		name: String,
		value: Any?,
	) {
		relations[name] = value
	}

	override fun getRelation(name: String): Any? = relations[name]

	override fun toString() = "${javaClass.simpleName}($id) [$attributes]"

}
