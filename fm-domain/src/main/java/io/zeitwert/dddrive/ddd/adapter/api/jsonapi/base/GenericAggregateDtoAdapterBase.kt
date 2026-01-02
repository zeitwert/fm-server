package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EnumProperty
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.PartMapProperty
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.ReferenceSetProperty
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericDto
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import java.math.BigDecimal

/**
 * Configuration for a relationship to be registered with the adapter.
 *
 * @param sourceProperty The name of the property on the aggregate (e.g., "mainContact", "logoImage")
 * @param dataSource Function to obtain the related ID(s) from the aggregate and DTO (either this or aggregatePropertyName must be provided)
 * @param targetRelation The name of the ID field on the DTO relation (e.g., "mainContactId", "logoId")
 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
 * @param isCollection Whether this is a collection relationship
 */
data class RelationshipConfig(
	val sourceProperty: String?,
	val dataSource: ((EntityWithProperties, GenericDto) -> Any?)?,
	val targetRelation: String,
	val resourceType: String,
	val isCollection: Boolean,
)

data class ReadableMap(
	val map: Map<String, Any?>,
) : GenericDto {

	override fun hasAttribute(name: String): Boolean = map.containsKey(name)

	override operator fun set(
		name: String,
		value: Any?,
	) = TODO()

	override operator fun get(name: String): Any? = map[name]

}

data class WritableMap(
	val map: MutableMap<String, Any?>,
) : GenericDto {

	override fun hasAttribute(name: String): Boolean = map.containsKey(name)

	override operator fun set(
		name: String,
		value: Any?,
	) {
		map[name] = value
	}

	override operator fun get(name: String): Any? = map[name]

}

/**
 * Generic adapter for converting between Aggregates and GenericResourceBase DTOs.
 *
 * Uses property metadata from EntityWithProperties to automatically serialize/deserialize
 * aggregate properties without requiring manual mapping code.
 *
 * Relationships must be explicitly registered via the fluent API.
 *
 * @param A The aggregate type
 * @param R The resource type (must extend GenericResourceBase)
 * @param resourceFactory Factory function to create new resource instances
 */
open class GenericAggregateDtoAdapterBase<A : Aggregate, R : GenericAggregateDto<A>>(
	private val directory: RepositoryDirectory,
	private val resourceFactory: () -> R,
) : AggregateDtoAdapter<A, R> {

	val tenantRepository get() = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository
	val userRepository get() = directory.getRepository(ObjUser::class.java) as ObjUserRepository

	private val exclusions = mutableListOf<String>()
	private val relationships = mutableListOf<RelationshipConfig>()

	// Properties to exclude from automatic serialization (handled separately)
	init {
		exclude(
			listOf(
				"id",
				"maxPartId",
				"objTypeId",
				"docTypeId",
				"version",
				"tenantId",
				"ownerId",
				"createdByUserId",
				"createdAt",
				"modifiedByUserId",
				"modifiedAt",
				"closedByUserId",
				"closedAt",
				"transitionList",
			),
		)
	}

	/**
	 * Exclude a property from automatic serialization.
	 *
	 * @param propertyName The name of the property to exclude
	 */
	fun exclude(propertyName: String) = exclusions.add(propertyName)

	fun exclude(propertyNames: List<String>) = exclusions.addAll(propertyNames)

	/**
	 * Register a single-value relationship.
	 *
	 * @param targetRelation The name of the ID field on the DTO (e.g., "mainContactId", "logoId")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
	 * @param sourceProperty The name of the aggregate property (e.g., "mainContact", "logoImage")
	 */
	fun relationship(
		targetRelation: String,
		resourceType: String,
		sourceProperty: String,
	) = addRelationship(targetRelation, resourceType, sourceProperty, null, false)

	/**
	 * Register a single-value relationship.
	 *
	 * @param targetRelation The name of the IDs field on the DTO (e.g., "mainContactId", "logoId")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
	 * @param dataSource Function to obtain the related ID from the aggregate
	 */
	fun relationship(
		targetRelation: String,
		resourceType: String,
		dataSource: (EntityWithProperties, GenericDto) -> Any?,
	) = addRelationship(targetRelation, resourceType, null, dataSource, false)

	/**
	 * Register a collection relationship.
	 *
	 * @param targetRelation The name of the IDs field on the DTO (e.g., "contactIds")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact")
	 * @param sourceProperty The name of the aggregate property (e.g., "contacts")
	 */
	fun relationshipSet(
		targetRelation: String,
		resourceType: String,
		sourceProperty: String,
	) = addRelationship(targetRelation, resourceType, sourceProperty, null, true)

	/**
	 * Register a collection relationship.
	 *
	 * @param targetRelation The name of the IDs field on the DTO (e.g., "contactIds")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact")
	 * @param dataSource Function to obtain the related IDs from the aggregate
	 */
	fun relationshipSet(
		targetRelation: String,
		resourceType: String,
		dataSource: (EntityWithProperties, GenericDto) -> List<Any>,
	) = addRelationship(targetRelation, resourceType, null, dataSource, true)

	private fun addRelationship(
		targetRelation: String,
		resourceType: String,
		sourceProperty: String?,
		dataSource: ((EntityWithProperties, GenericDto) -> Any?)?,
		isCollection: Boolean,
	) = relationships.add(RelationshipConfig(sourceProperty, dataSource, targetRelation, resourceType, isCollection))

	private fun Property<*>.isExcluded(): Boolean = name in exclusions || relationships.any { name == it.sourceProperty }

	/**
	 * Convert an aggregate to a resource DTO.
	 */
	override fun fromAggregate(
		aggregate: A,
	): R {
		val dto = resourceFactory()
		fromEntity(
			entity = aggregate as EntityWithProperties,
			properties = aggregate.properties.filter { !it.isExcluded() },
			dto = dto,
		)
		fromRelationships(aggregate, dto)
		return dto
	}

	private fun fromEntity(
		entity: EntityWithProperties,
		properties: List<Property<*>>,
		dto: GenericDto,
	) {
		for (property in properties) {
			when (property) {
				is PartListProperty<*, *> -> {
					dto[property.name] = property.map { fromPart(it) }
				}

				is PartMapProperty<*, *> -> {
					dto[property.name] =
						property.entries.associate { (key, p) ->
							key to fromPart(p)
						}
				}

				is EnumSetProperty<*> -> {
					dto[property.name] = property.map { EnumeratedDto.of(it) }
				}

				is ReferenceSetProperty<*> -> {
					dto["${property.name}Ids"] = property.toSet()
				}

				is AggregateReferenceProperty<*> -> {
					dto["${property.name}Id"] = property.id?.toString()
				}

				is PartReferenceProperty<*, *> -> {
					dto["${property.name}Id"] = property.id?.toString()
				}

				is EnumProperty<*> -> {
					dto[property.name] = EnumeratedDto.of(property.value)
				}

				is BaseProperty<*> -> {
					dto[property.name] = property.value
				}
			}
		}
	}

	private fun fromPart(part: Part<*>): Map<String, Any?> {
		val dto = WritableMap(mutableMapOf())
		dto["id"] = part.id.toString()
		fromEntity(part as EntityWithProperties, part.properties, dto)
		return dto.map
	}

	/**
	 * Populate relationship ID fields on the DTO based on registered relationships.
	 * Uses reflection to set fields declared on the concrete resource class.
	 */
	private fun fromRelationships(
		entity: EntityWithProperties,
		dto: R,
	) {
		for (rel in relationships) {
			if (rel.dataSource != null) {
				dto.setRelation(rel.targetRelation, rel.dataSource.invoke(entity, dto))
			} else if (rel.sourceProperty != null) {
				when (val property = entity.getProperty(rel.sourceProperty, Any::class)) {
					is AggregateReferenceProperty<*> -> {
						val id: String? = property.id?.toString()
						if (id != null) {
							dto.setRelation(rel.targetRelation, id)
						}
					}

					is ReferenceSetProperty<*> -> {
						val ids: List<String> = property.map { it.toString() }
						dto.setRelation(rel.targetRelation, ids)
					}

					is BaseProperty<*> -> {
						val id: String? = property.value?.toString()
						if (id != null) {
							dto.setRelation(rel.targetRelation, id)
						}
					}

					else -> {
						null
					}
				}
			}
		}
	}

	/**
	 * Apply DTO values to an aggregate.
	 */
	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: R,
		aggregate: A,
	) {
		aggregate as EntityWithProperties
		val properties = aggregate.properties.filter { !it.isExcluded() && it.isWritable }
		toEntity(dto, aggregate, properties)
	}

	/**
	 * Apply DTO values to a part.
	 */
	@Suppress("UNCHECKED_CAST")
	fun toPart(
		dto: GenericDto,
		part: Part<*>,
	) {
		toEntity(dto, part as EntityWithProperties, part.properties)
	}

	/**
	 * Apply DTO values to an aggregate.
	 */
	@Suppress("UNCHECKED_CAST")
	fun toEntity(
		dto: GenericDto,
		entity: EntityWithProperties,
		properties: List<Property<*>>,
	) {
		for (property in properties) {
			when (property) {
				is PartListProperty<*, *> -> {
					toPartList(dto, property)
				}

				is PartMapProperty<*, *> -> {
					toPartMap(dto, property)
				}

				is EnumSetProperty<*> -> {
					toEnumSet(dto, property as EnumSetProperty<Enumerated>)
				}

				is ReferenceSetProperty<*> -> {
					toReferenceSet(dto, property)
				}

				is AggregateReferenceProperty<*> -> {
					property.id = (dto["${property.name}Id"] as String?)?.toInt()
				}

				is PartReferenceProperty<*, *> -> {
					property.id = (dto["${property.name}Id"] as String?)?.toInt()
				}

				is EnumProperty<*> -> {
					property.id = (dto[property.name] as Map<String, Any?>)["id"] as? String
				}

				is BaseProperty<*> -> {
					(property as BaseProperty<Any>).value = toDomainValue(dto[property.name], property.type)
				}
			}
		}
	}

	/**
	 * Deserialize a part list from DTO.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toPartList(
		dto: GenericDto,
		property: PartListProperty<*, *>,
	) {
		val parts = dto[property.name] as? List<Map<String, Any?>> ?: return
		property.clear()
		for (partValues in parts) {
			val partId = partValues["id"] as? Int // TODO creation
			val part = property.add(partId)
			toPart(ReadableMap(partValues), part)
		}
	}

	/**
	 * Deserialize a part map from DTO.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toPartMap(
		dto: GenericDto,
		property: PartMapProperty<*, *>,
	) {
		val parts = dto[property.name] as? Map<String, Map<String, Any?>> ?: return
		property.clear()
		for ((key, partValues) in parts) {
			val partId = partValues["id"] as? Int // TODO creation
			val part = property.add(key, partId)
			toPart(ReadableMap(partValues), part)
		}
	}

	/**
	 * Deserialize an enum set from DTO.
	 *
	 * Note: EnumSetProperty deserialization requires access to the enumeration,
	 * which is obtained from the implementation class. If not available, we skip
	 * this property.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toEnumSet(
		dto: GenericDto,
		property: EnumSetProperty<Enumerated>,
	) {
		val values = dto[property.name] as? List<*> ?: return
		property.clear()
		for (value in values) {
			val enumId = (value as Map<String, Any?>)["id"] as? String
			if (enumId != null) {
				property.add(property.enumeration.getItem(enumId))
			}
		}
	}

	/**
	 * Deserialize a reference set from DTO.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toReferenceSet(
		dto: GenericDto,
		property: ReferenceSetProperty<*>,
	) {
		val ids = dto["${property.name}Ids"] as? Collection<Any> ?: return
		property.clear()
		for (id in ids) {
			property.add(id)
		}
	}

	/**
	 * Convert a value to the expected type.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun <T : Any> toDomainValue(
		value: Any?,
		targetType: Class<T>,
	): T? {
		if (value == null) return null

		return when {
			targetType.isInstance(value) -> {
				value as T
			}

			targetType == BigDecimal::class.java && value is Number -> {
				BigDecimal(value.toString()) as T
			}

			targetType == Int::class.java && value is Number -> {
				value.toInt() as T
			}

			targetType == Long::class.java && value is Number -> {
				value.toLong() as T
			}

			targetType == Double::class.java && value is Number -> {
				value.toDouble() as T
			}

			targetType == String::class.java -> {
				value.toString() as T
			}

			else -> {
				value as? T
			}
		}
	}

}
