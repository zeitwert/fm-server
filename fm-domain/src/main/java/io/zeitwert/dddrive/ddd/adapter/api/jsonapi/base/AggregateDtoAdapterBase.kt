package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.AggregateReferenceSetProperty
import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EnumProperty
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.PartMapProperty
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.Property
import io.crnk.core.resource.meta.MetaInformation
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.JsonApiDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// ============================================================================
// Helper Classes for DTO Wrapping
// ============================================================================

data class ReadableMap(
	val map: Map<String, Any?>,
) : JsonApiDto {

	override fun hasAttribute(name: String): Boolean = map.containsKey(name)

	override operator fun set(
		name: String,
		value: Any?,
	) = TODO()

	override operator fun get(name: String): Any? = map[name]
}

data class WritableMap(
	val map: MutableMap<String, Any?>,
) : JsonApiDto {

	override fun hasAttribute(name: String): Boolean = map.containsKey(name)

	override operator fun set(
		name: String,
		value: Any?,
	) {
		map[name] = value
	}

	override operator fun get(name: String): Any? = map[name]
}

class MetaInfo :
	HashMap<String, Any?>(),
	JsonApiDto,
	MetaInformation {

	override fun hasAttribute(name: String): Boolean = containsKey(name)

	override operator fun set(
		name: String,
		value: Any?,
	) {
		super.put(name, value)
	}

	override operator fun get(name: String): Any? = super.get(name)
}

// ============================================================================
// Aggregate DTO Adapter
// ============================================================================

/**
 * Generic adapter for converting between Aggregates and GenericResourceBase DTOs.
 *
 * Uses property metadata from EntityWithProperties to automatically serialize/deserialize aggregate
 * properties without requiring manual mapping code.
 *
 * Configuration is provided via a DSL block that configures an [AggregateDtoAdapterConfig].
 *
 * @param A The aggregate type
 * @param R The resource type (must extend GenericResourceBase)
 * @param directory The repository directory for loading related entities
 * @param resourceFactory Factory function to create new resource instances
 * @param configure Optional DSL block to configure the adapter
 */
open class AggregateDtoAdapterBase<A : Aggregate, R : AggregateDto<A>>(
	private val directory: RepositoryDirectory,
	private val resourceFactory: () -> R,
	configure: AggregateDtoAdapterConfig.() -> Unit = {},
) : AggregateDtoAdapter<A, R> {

	companion object {

		val logger = LoggerFactory.getLogger(AggregateDtoAdapterBase::class.java)
	}

	val tenantRepository
		get() = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository
	val userRepository
		get() = directory.getRepository(ObjUser::class.java) as ObjUserRepository

	/**
	 * The configuration for this adapter. Subclasses can access this to add additional configuration
	 * in their init blocks.
	 */
	protected val config: AggregateDtoAdapterConfig = AggregateDtoAdapterConfig().apply(configure)

	/** Convert an aggregate to a resource DTO. */
	override fun fromAggregate(
		aggregate: A,
	): R {
		val dto = resourceFactory()
		dto["id"] = DtoUtils.idToString(aggregate.id)
		val meta = MetaInfo()
		fromFields(aggregate as EntityWithProperties, meta, config.metas)
		(dto as AggregateDtoBase<*>).meta = meta
		fromEntity(
			entity = aggregate,
			properties = aggregate.properties.filter { !config.run { it.isExcluded() } },
			dto = dto,
		)
		fromRelationships(aggregate, dto)
		fromFields(aggregate, dto, config.fields)
		return dto
	}

	/**
	 * Convert a part to a map for JSON serialization.
	 *
	 * If a part adapter is registered for the part's class, it will be used to:
	 * - Exclude specified properties from serialization
	 * - Add custom computed fields
	 *
	 * Parts without explicit configuration are serialized using the generic infrastructure.
	 */
	protected open fun fromPart(part: Part<*>): Map<String, Any?> {
		val dto = WritableMap(mutableMapOf())
		dto["id"] = part.id.toString()
		val entity = part as EntityWithProperties
		val partConfig = config.findPartAdapterConfig(part.javaClass)
		logger.trace(
			"fromPart: ${part.javaClass.simpleName}, config: ${partConfig?.exclusions?.size ?: 0}, ${partConfig?.fields?.size ?: 0}",
		)
		val properties =
			if (partConfig != null) {
				entity.properties.filter {
					it.name !in partConfig.exclusions &&
						!partConfig.fields.any { f -> it.name == f.sourceProperty }
				}
			} else {
				entity.properties
			}
		fromEntity(entity, properties, dto)
		fromFields(entity, dto, partConfig?.fields ?: emptyList())
		return dto.map
	}

	private fun fromEntity(
		entity: EntityWithProperties,
		properties: List<Property<*>>,
		dto: JsonApiDto,
	) {
		logger.trace("fromEntity: {}, properties: {}", entity, properties.map { it.name })
		for (property in properties) {
			try {
				val fieldName =
					when (property) {
						is AggregateReferenceProperty<*> -> "${property.name}Id"
						is PartReferenceProperty<*, *> -> "${property.name}Id"
						else -> property.name
					}
				logger.trace("fromEntity[{}] = {}", fieldName, property)
				when (property) {
					is PartListProperty<*, *> -> {
						dto[fieldName] = property.map { fromPart(it) }
					}

					is PartMapProperty<*, *> -> {
						dto[fieldName] = property.entries.associate { (key, p) -> key to fromPart(p) }
					}

					is EnumSetProperty<*> -> {
						dto[fieldName] = property.map { EnumeratedDto.of(it) }
					}

					is AggregateReferenceSetProperty<*> -> {
						dto[fieldName] = property.toSet()
					}

					is AggregateReferenceProperty<*> -> {
						dto[fieldName] = DtoUtils.idToString(property.id)
					}

					is PartReferenceProperty<*, *> -> {
						dto[fieldName] = property.id?.toString()
					}

					is EnumProperty<*> -> {
						dto[fieldName] = EnumeratedDto.of(property.value)
					}

					is BaseProperty<*> -> {
						dto[fieldName] = fromDomainValue(property.value, property.type)
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

	/**
	 * Populate relationship ID fields on the DTO based on registered relationships. Uses reflection
	 * to set fields declared on the concrete resource class.
	 */
	private fun fromRelationships(
		entity: EntityWithProperties,
		dto: AggregateDto<*>,
	) {
		for (rel in config.relationships) {
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

						is AggregateReferenceSetProperty<*> -> {
							val ids: List<String> = property.map { DtoUtils.idToString(it)!! }
							dto.setRelation(rel.targetRelation, ids)
						}

						is BaseProperty<*> -> {
							dto.setRelation(rel.targetRelation, DtoUtils.idToString(property.value))
						}

						else -> {
							throw IllegalArgumentException(
								"[${entity.javaClass.simpleName}.${rel.targetRelation}] Unsupported property type for relationship mapping ${entity.javaClass.simpleName}.${rel.sourceProperty}: ${property.javaClass.name}",
							)
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
	 * Populate field values on the DTO based on registered field mappings. Uses intelligent type
	 * detection for simple mappings.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun fromFields(
		entity: EntityWithProperties,
		dto: JsonApiDto,
		fields: List<FieldConfig>,
	) {
		for (fieldConfig in fields) {
			try {
				if (fieldConfig.outgoing != null) {
					dto[fieldConfig.targetField] = fieldConfig.outgoing.invoke(entity)
				} else if (fieldConfig.sourceProperty != null) {
					val fieldName = fieldConfig.targetField
					val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)
					logger.trace(
						"fromField: {}.{}: {} -> {}",
						entity.javaClass.simpleName,
						fieldConfig.sourceProperty,
						property.javaClass.simpleName,
						fieldName,
					)
					when (property) {
						is PartListProperty<*, *> -> {
							dto[fieldName] = property.map { fromPart(it) }
						}

						is PartMapProperty<*, *> -> {
							dto[fieldName] = property.entries.associate { (key, p) -> key to fromPart(p) }
						}

						is EnumSetProperty<*> -> {
							dto[fieldName] = property.map { EnumeratedDto.of(it) }
						}

						is AggregateReferenceSetProperty<*> -> {
							val repo = directory.getRepository(property.aggregateType)
							val enumDtos =
								property.map { id ->
									val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
									EnumeratedDto.of(aggregate)
								}
							dto[fieldName] = enumDtos
						}

						is AggregateReferenceProperty<*> -> {
							val id = property.id
							if (id != null) {
								val repo = directory.getRepository(property.aggregateType)
								val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
								dto[fieldName] = EnumeratedDto.of(aggregate)
							} else {
								dto[fieldName] = null
							}
						}

						is PartReferenceProperty<*, *> -> {
							dto[fieldName] = property.id?.toString()
						}

						is EnumProperty<*> -> {
							dto[fieldName] = EnumeratedDto.of(property.value)
						}

						is BaseProperty<*> -> {
							dto[fieldName] = fromDomainValue(property.value, property.type)
						}

						else -> {
							throw IllegalArgumentException(
								"[${entity.javaClass.simpleName}.$fieldName] Unsupported property type for field mapping ${entity.javaClass.simpleName}.${fieldConfig.sourceProperty}: ${property.javaClass.name}",
							)
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

	/** Apply DTO values to an aggregate. */
	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		dto: R,
		aggregate: A,
	) {
		aggregate as EntityWithProperties
		val properties =
			aggregate.properties.filter { !config.run { it.isExcluded() } && it.isWritable }
		logger.trace(
			"toAggregate: {} from {}, properties: {}",
			aggregate,
			dto,
			properties.map { it.name },
		)
		toEntity(dto, aggregate, properties)
		toFields(dto, aggregate)
	}

	/**
	 * Apply DTO values to a part.
	 *
	 * If a part adapter is registered for the part's class, it will be used to:
	 * - Exclude specified properties from deserialization
	 * - Apply custom incoming field handlers
	 *
	 * Parts without explicit configuration are deserialized using the generic infrastructure.
	 */
	@Suppress("UNCHECKED_CAST")
	fun toPart(
		dto: JsonApiDto,
		part: Part<*>,
	) {
		val entity = part as EntityWithProperties
		val partConfig = config.findPartAdapterConfig(part.javaClass)
		val properties =
			if (partConfig != null) {
				entity.properties.filter { it.name !in partConfig.exclusions && it.isWritable }
			} else {
				entity.properties.filter { it.isWritable }
			}

		toEntity(dto, entity, properties)

		// Apply custom incoming fields from part adapter config
		partConfig?.fields?.forEach { fieldConfig ->
			if (fieldConfig.incoming != null && dto.hasAttribute(fieldConfig.targetField)) {
				try {
					fieldConfig.incoming.invoke(dto[fieldConfig.targetField], part)
				} catch (ex: Exception) {
					throw RuntimeException(
						"[${part.javaClass.simpleName}.${fieldConfig.targetField}] toPart field crashed: ${ex.message}",
						ex,
					)
				}
			}
		}
	}

	/** Apply DTO values to an aggregate. */
	@Suppress("UNCHECKED_CAST")
	fun toEntity(
		dto: JsonApiDto,
		entity: EntityWithProperties,
		properties: List<Property<*>>,
	) {
		for (property in properties) {
			val fieldName =
				when (property) {
					is AggregateReferenceProperty<*> -> "${property.name}Id"
					is PartReferenceProperty<*, *> -> "${property.name}Id"
					else -> property.name
				}
			logger.trace(
				"toEntity.property: {} from dto field {}: {}",
				property.name,
				fieldName,
				dto[fieldName],
			)
			if (!dto.hasAttribute(fieldName)) continue
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

					is AggregateReferenceSetProperty<*> -> {
						toReferenceSet(dto, property)
					}

					is AggregateReferenceProperty<*> -> {
						property.id = DtoUtils.idFromString(dto[fieldName] as String?)
					}

					is PartReferenceProperty<*, *> -> {
						property.id = (dto[fieldName] as String?)?.toInt()
					}

					is EnumProperty<*> -> {
						property.id = dto.enumId(fieldName)
					}

					is BaseProperty<*> -> {
						(property as BaseProperty<Any>).value = toDomainValue(dto[fieldName], property.type)
					}
				}
			} catch (ex: Exception) {
				throw RuntimeException(
					"toEntity(${entity.javaClass.simpleName}.${property.name}) crashed: ${ex.message}",
					ex,
				)
			}
		}
	}

	/**
	 * Apply field values from DTO to entity based on registered field mappings. Uses intelligent type
	 * detection for simple mappings.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toFields(
		dto: JsonApiDto,
		entity: EntityWithProperties,
	) {
		for (fieldConfig in config.fields) {
			if (!dto.hasAttribute(fieldConfig.targetField)) continue
			val dtoValue = dto[fieldConfig.targetField]
			try {
				if (fieldConfig.incoming != null) {
					fieldConfig.incoming.invoke(dtoValue, entity)
				} else if (fieldConfig.sourceProperty != null) {
					when (val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)) {
						is AggregateReferenceSetProperty<*> -> {
							val values = dtoValue as List<*>
							property.clear()
							for (value in values) {
								val id = dto.enumId(value)
								if (id != null) {
									property.add(DtoUtils.idFromString(id)!!)
								}
							}
						}

						is AggregateReferenceProperty<*> -> {
							property.id = DtoUtils.idFromString(dto.enumId(dtoValue))
						}

						is EnumProperty<*> -> {
							property.id = dto.enumId(dtoValue)
						}

						is BaseProperty<*> -> {
							(property as BaseProperty<Any>).value = toDomainValue(dtoValue, property.type)
						}

						else -> {
							throw IllegalArgumentException(
								"[${entity.javaClass.simpleName}.${fieldConfig.targetField}] Unsupported property type for field mapping ${entity.javaClass.simpleName}.${fieldConfig.sourceProperty}: ${property.javaClass.name}",
							)
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

	/** Deserialize a part list from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toPartList(
		dto: JsonApiDto,
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

	/** Deserialize a part map from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toPartMap(
		dto: JsonApiDto,
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
	 * Note: EnumSetProperty deserialization requires access to the enumeration, which is obtained
	 * from the implementation class. If not available, we skip this property.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun toEnumSet(
		dto: JsonApiDto,
		property: EnumSetProperty<Enumerated>,
	) {
		val values = dto[property.name] as? List<*> ?: return
		property.clear()
		for (value in values) {
			val enumId = dto.enumId(value)
			if (enumId != null) {
				property.add(property.enumeration.getItem(enumId))
			}
		}
	}

	/** Deserialize a reference set from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toReferenceSet(
		dto: JsonApiDto,
		property: AggregateReferenceSetProperty<*>,
	) {
		val ids = dto[property.name] as? Collection<String> ?: return
		property.clear()
		for (id in ids) {
			property.add(DtoUtils.idFromString(id)!!)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T : Any> fromDomainValue(
		value: Any?,
		sourceType: Class<T>,
	): Any? {
		if (value == null) return null

		return when (sourceType) {
			LocalDate::class.java -> {
				(value as LocalDate).format(DateTimeFormatter.ISO_DATE)
			}

			OffsetDateTime::class.java -> {
				(value as OffsetDateTime).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
			}

			else -> {
				value
			}
		}
	}

	/** Convert a value to the expected type. */
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

			targetType == LocalDate::class.java -> {
				// Expecting ISO date string, strip time if datetime provided
				LocalDate.parse(value.toString().substring(0, 10), DateTimeFormatter.ISO_DATE) as T
			}

			targetType == OffsetDateTime::class.java -> {
				OffsetDateTime.parse(value.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME) as T
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
