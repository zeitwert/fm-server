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
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
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

/**
 * Configuration for a field mapping to be registered with the adapter.
 *
 * @param targetField The name of the field on the DTO (e.g., "tenants")
 * @param sourceProperty The name of the property on the aggregate (e.g., "tenantSet"), or null if using custom functions
 * @param outgoing Function to compute the DTO value from the entity (for fromAggregate)
 * @param incoming Function to apply the DTO value to the entity (for toAggregate)
 */
data class FieldConfig(
	val targetField: String,
	val sourceProperty: String?,
	val outgoing: ((EntityWithProperties) -> Any?)?,
	val incoming: ((Any?, EntityWithProperties) -> Unit)?,
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
	private val fields = mutableListOf<FieldConfig>()

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

	/**
	 * Register a field mapping from a source property to a target field.
	 *
	 * Uses intelligent type detection:
	 * - ReferenceSetProperty -> List<EnumeratedDto> (loads each entity)
	 * - AggregateReferenceProperty -> EnumeratedDto (loads entity)
	 * - Other properties -> direct value copy
	 *
	 * @param targetField The name of the field on the DTO (e.g., "tenants")
	 * @param sourceProperty The name of the property on the aggregate (e.g., "tenantSet")
	 */
	fun field(
		targetField: String,
		sourceProperty: String,
	) = fields.add(FieldConfig(targetField, sourceProperty, null, null))

	/**
	 * Register a field with custom outgoing and incoming functions.
	 *
	 * @param targetField The name of the field on the DTO
	 * @param outgoing Function to compute the DTO value from the entity (for fromAggregate)
	 * @param incoming Function to apply the DTO value to the entity (for toAggregate)
	 */
	fun field(
		targetField: String,
		outgoing: (EntityWithProperties) -> Any?,
		incoming: ((Any?, EntityWithProperties) -> Unit)? = null,
	) = fields.add(FieldConfig(targetField, null, outgoing, incoming))

	private fun Property<*>.isExcluded(): Boolean =
		name in exclusions ||
			relationships.any { name == it.sourceProperty } ||
			fields.any { name == it.sourceProperty }

	/**
	 * Convert an aggregate to a resource DTO.
	 */
	override fun fromAggregate(
		aggregate: A,
	): R {
		val dto = resourceFactory()
		dto["id"] = DtoUtils.idToString(aggregate.id)
		fromEntity(
			entity = aggregate as EntityWithProperties,
			properties = aggregate.properties.filter { !it.isExcluded() },
			dto = dto,
		)
		fromRelationships(aggregate, dto)
		fromFields(aggregate, dto)
		return dto
	}

	private fun fromEntity(
		entity: EntityWithProperties,
		properties: List<Property<*>>,
		dto: GenericDto,
	) {
		for (property in properties) {
			try {
				when (property) {
					is PartListProperty<*, *> -> {
						dto[property.name] = property.map { fromPart(it) }
					}

					is PartMapProperty<*, *> -> {
						dto[property.name] = property.entries.associate { (key, p) -> key to fromPart(p) }
					}

					is EnumSetProperty<*> -> {
						dto[property.name] = property.map { EnumeratedDto.of(it) }
					}

					is ReferenceSetProperty<*> -> {
						dto["${property.name}Ids"] = property.toSet()
					}

					is AggregateReferenceProperty<*> -> {
						dto["${property.name}Id"] = DtoUtils.idToString(property.id)
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
			} catch (ex: Exception) {
				throw RuntimeException(
					"[${entity.javaClass.simpleName}.${property.name}] fromProperty crashed: ${ex.message}",
					ex,
				)
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
			try {
				if (rel.dataSource != null) {
					dto.setRelation(rel.targetRelation, rel.dataSource.invoke(entity, dto))
				} else if (rel.sourceProperty != null) {
					when (val property = entity.getProperty(rel.sourceProperty, Any::class)) {
						is AggregateReferenceProperty<*> -> {
							val id: String? = DtoUtils.idToString(property.id)
							if (id != null) {
								dto.setRelation(rel.targetRelation, id)
							}
						}

						is ReferenceSetProperty<*> -> {
							val ids: List<String> = property.map { DtoUtils.idToString(it) }
							dto.setRelation(rel.targetRelation, ids)
						}

						is BaseProperty<*> -> {
							dto.setRelation(rel.targetRelation, DtoUtils.idToString(property.value))
						}

						else -> {
							throw IllegalArgumentException("[${entity.javaClass.simpleName}.${rel.targetRelation}] Unsupported property type for relationship mapping ${entity.javaClass.simpleName}.${rel.sourceProperty}: ${property.javaClass.name}")
						}
					}
				}
			} catch (ex: Exception) {
				throw RuntimeException(
					"[${entity.javaClass.simpleName}.${rel.targetRelation}] fromRelationship crashed: ${ex.message}",
					ex,
				)
			}
		}
	}

	/**
	 * Populate field values on the DTO based on registered field mappings.
	 * Uses intelligent type detection for simple mappings.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun fromFields(
		entity: EntityWithProperties,
		dto: R,
	) {
		for (fieldConfig in fields) {
			try {
				if (fieldConfig.outgoing != null) {
					// Custom outgoing function
					dto[fieldConfig.targetField] = fieldConfig.outgoing.invoke(entity)
				} else if (fieldConfig.sourceProperty != null) {
					// Simple mapping with intelligent type detection
					when (val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)) {
						is ReferenceSetProperty<*> -> {
							// Convert reference set to List<EnumeratedDto>
							val repo = directory.getRepository(property.targetClass)
							val enumDtos = property.mapNotNull { id ->
								val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
								if (aggregate != null) EnumeratedDto.of(aggregate) else null
							}
							dto[fieldConfig.targetField] = enumDtos
						}

						is AggregateReferenceProperty<*> -> {
							// Convert aggregate reference to EnumeratedDto
							val id = property.id
							if (id != null) {
								val repo = directory.getRepository(property.targetClass)
								val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
								dto[fieldConfig.targetField] = EnumeratedDto.of(aggregate)
							} else {
								dto[fieldConfig.targetField] = null
							}
						}

						is BaseProperty<*> -> {
							// Direct value copy
							dto[fieldConfig.targetField] = property.value
						}

						else -> {
							throw IllegalArgumentException("[${entity.javaClass.simpleName}.${fieldConfig.targetField}] Unsupported property type for field mapping ${entity.javaClass.simpleName}.${fieldConfig.sourceProperty}: ${property.javaClass.name}")
						}
					}
				}
			} catch (ex: Exception) {
				throw RuntimeException(
					"[${entity.javaClass.simpleName}.${fieldConfig.targetField}] fromField crashed: ${ex.message}",
					ex,
				)
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
		toFields(dto, aggregate)
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
			if (!dto.hasAttribute(property.name)) continue
			try {
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
						property.id = DtoUtils.idFromString(dto["${property.name}Id"] as String?)
					}

					is PartReferenceProperty<*, *> -> {
						property.id = (dto["${property.name}Id"] as String?)?.toInt()
					}

					is EnumProperty<*> -> {
						property.id = (dto[property.name] as Map<String, Any?>?)?.get("id") as? String
					}

					is BaseProperty<*> -> {
						(property as BaseProperty<Any>).value = toDomainValue(dto[property.name], property.type)
					}
				}
			} catch (ex: Exception) {
				throw RuntimeException("toEntity(${entity.javaClass.simpleName}.${property.name}) crashed: ${ex.message}", ex)
			}
		}
	}

	/**
	 * Apply field values from DTO to entity based on registered field mappings.
	 * Uses intelligent type detection for simple mappings.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toFields(
		dto: GenericDto,
		entity: EntityWithProperties,
	) {
		for (fieldConfig in fields) {
			if (!dto.hasAttribute(fieldConfig.targetField)) continue
			val dtoValue = dto[fieldConfig.targetField]
			try {
				if (fieldConfig.incoming != null) {
					fieldConfig.incoming.invoke(dtoValue, entity)
				} else if (fieldConfig.sourceProperty != null) {
					when (val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)) {
						is ReferenceSetProperty<*> -> {
							val values = dtoValue as List<*>
							property.clear()
							for (value in values) {
								val id = when (value) {
									is Map<*, *> -> DtoUtils.idFromString(value["id"] as? String)
									else -> throw IllegalArgumentException("Invalid value type for ReferenceSetProperty: ${value?.javaClass?.name}")
								}
								if (id != null) {
									property.add(id)
								}
							}
						}

						is AggregateReferenceProperty<*> -> {
							val id = when (dtoValue) {
								null -> null
								is Map<*, *> -> DtoUtils.idFromString(dtoValue["id"] as String?)
								else -> throw IllegalArgumentException("Invalid value type for ReferenceProperty: ${dtoValue.javaClass.name}")
							}
							property.id = id
						}

						is BaseProperty<*> -> {
							(property as BaseProperty<Any>).value = toDomainValue(dtoValue, property.type)
						}

						else -> {
							throw IllegalArgumentException("[${entity.javaClass.simpleName}.${fieldConfig.targetField}] Unsupported property type for field mapping ${entity.javaClass.simpleName}.${fieldConfig.sourceProperty}: ${property.javaClass.name}")
						}
					}
				}
			} catch (ex: Exception) {
				throw RuntimeException(
					"toField(${entity.javaClass.simpleName}.${fieldConfig.targetField}) crashed: ${ex.message}",
					ex,
				)
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
		val ids = dto[property.name] as? Collection<String> ?: return
		property.clear()
		for (id in ids) {
			property.add(DtoUtils.idFromString(id))
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
