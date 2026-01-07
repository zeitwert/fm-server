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
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.JsonDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.AggregateDtoAdapterConfig
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.FieldConfig
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.RelationshipConfig
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.ResourceEntry
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.ResourceRegistry
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.MapDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.MetaInfoDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.MutableMapDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Generic adapter for converting between Aggregate and JsonDto.
 *
 * Uses property metadata from EntityWithProperties to automatically serialize/deserialize aggregate
 * properties without requiring manual mapping code.
 *
 * Non-generic fields can be mapped through configuration via a config DSL of [io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config.AggregateDtoAdapterConfig].
 *
 * @param A The aggregate type
 * @param R The resource type (must extend JsonDto)
 * @param aggregateClass The aggregate class (for registry registration)
 * @param resourceType The JSON API resource type (e.g., "account", "contact")
 * @param dtoClass The DTO class (for registry registration)
 * @param directory The repository directory for loading related entities
 * @param resourceFactory Factory function to create new resource instances
 */
open class AggregateDtoAdapterBase<A : Aggregate, R : AggregateDto<A>>(
	private val aggregateClass: Class<A>,
	private val resourceType: String,
	private val dtoClass: Class<out AggregateDtoBase<*>>,
	private val directory: RepositoryDirectory,
	private val resourceFactory: () -> R,
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
	val config: AggregateDtoAdapterConfig = AggregateDtoAdapterConfig()

	init {
		// Self-register with the resource registry
		@Suppress("UNCHECKED_CAST")
		ResourceRegistry.register(
			ResourceEntry(
				aggregateClass = aggregateClass as Class<out dddrive.app.ddd.model.Aggregate>,
				resourceType = resourceType,
				dtoClass = dtoClass,
				adapter = this,
			),
		)

		config.exclude(listOf("id", "maxPartId"))
		config.field("tenant")
		config.field("owner")
		config.meta(
			listOf(
				"tenant",
				"owner",
				"version",
				"createdByUser",
				"createdAt",
				"modifiedByUser",
				"modifiedAt",
			),
		)
		config.relationship("tenantInfo", "tenant", "tenant")
		config.relationship("account", "account", "account")
	}

	/** Convert an aggregate to a resource DTO. */
	override fun fromAggregate(
		aggregate: A,
	): R {
		aggregate as EntityWithProperties
		val dto = resourceFactory()
		dto["id"] = DtoUtils.idToString(aggregate.id)
		val meta = MetaInfoDto()
		fromFields(aggregate as EntityWithProperties, meta, config.metas.values)
		(dto as AggregateDtoBase<*>).meta = meta
		val properties = aggregate.properties.filter { !config.isExcluded(it) }
		logger.trace("fromAggregate: {}", aggregate)
		logger.trace(". config: {exclusions: ${config.exclusions.size}, fields: ${config.fields.size}, relationships: ${config.relationships.size}, metas: ${config.metas.size}}")
		logger.trace(". properties: {}", properties.map { it.name })
		fromEntity(aggregate, properties, dto)
		fromRelationships(aggregate, dto, config.relationships.values)
		fromFields(aggregate, dto, config.fields.values)
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
		part as EntityWithProperties
		val dto = MutableMapDto()
		dto["id"] = part.id.toString()
		val partConfig = config.partAdapterConfig(part.javaClass)
		val properties = part.properties.filter { !partConfig.isExcluded(it) }
		logger.trace(". fromPart: {}", part)
		logger.trace(".   properties: {}", properties.map { it.name })
		logger.trace(".   config: {exclusions: ${partConfig.exclusions.size}, fields: ${partConfig.fields.size}}")
		fromEntity(part, properties, dto)
		fromFields(part, dto, partConfig.fields.values)
		return dto
	}

	private fun fromEntity(
		entity: EntityWithProperties,
		properties: List<Property<*>>,
		dto: JsonDto,
	) {
		val indent = if (entity is Part<*>) ".     " else ".   "
		for (property in properties) {
			try {
				dto[property.name] = fromProperty(property)
				logger.trace(
					"${indent}dto[{}]: {} = fromProperty({}, {})",
					property.name,
					dto[property.name],
					property,
					property.javaClass.simpleName,
				)
			} catch (ex: Exception) {
				throw RuntimeException(
					"[${entity.javaClass.simpleName}.${property.name}] fromProperty crashed: ${ex.message}",
					ex,
				)
			}
		}
	}

	/** Convert a property value to its DTO representation */
	private fun fromProperty(
		property: Property<*>,
		doInline: Boolean = false,
	): Any? =
		when (property) {
			is AggregateReferenceProperty<*> -> {
				val id = property.id ?: return null
				val repo = directory.getRepository(property.aggregateType)
				val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
				EnumeratedDto.of(aggregate)
			}

			is AggregateReferenceSetProperty<*> -> {
				val repo = directory.getRepository(property.aggregateType)
				property.map { id ->
					val aggregate = repo.get(id) as dddrive.app.ddd.model.Aggregate
					EnumeratedDto.of(aggregate)
				}
			}

			is PartReferenceProperty<*, *> -> {
				if (doInline) {
					property.value?.let { fromPart(it) }
				} else {
					property.id?.toString()
				}
			}

			is PartListProperty<*, *> -> {
				property.map { fromPart(it) }
			}

			is PartMapProperty<*, *> -> {
				property.entries.associate { (key, p) -> key to fromPart(p) }
			}

			is EnumProperty<*> -> {
				EnumeratedDto.of(property.value)
			}

			is EnumSetProperty<*> -> {
				property.map { EnumeratedDto.of(it) }
			}

			is BaseProperty<*> -> {
				fromDomainValue(property.value, property.type)
			}

			else -> {
				throw IllegalArgumentException(
					"Unsupported property type: ${property.javaClass.name}",
				)
			}
		}

	/**
	 * Populate relationship ID fields on the DTO based on registered relationships. Uses reflection
	 * to set fields declared on the concrete resource class.
	 */
	private fun fromRelationships(
		entity: EntityWithProperties,
		dto: AggregateDto<*>,
		relationships: Collection<RelationshipConfig>,
	) {
		val indent = if (entity is Part<*>) ".     " else ".   "
		for (rel in relationships) {
			try {
				if (rel.dataSource != null) {
					dto.setRelation(rel.targetRelation, rel.dataSource.invoke(entity, dto))
					logger.trace("${indent}dto.relation[{}]: {}", rel.targetRelation, dto.getRelation(rel.targetRelation))
				} else if (rel.sourceProperty != null) {
					val property = entity.getProperty(rel.sourceProperty, Any::class)
					when (property) {
						is AggregateReferenceProperty<*> -> {
							dto.setRelation(rel.targetRelation, DtoUtils.idToString(property.id))
						}

						is AggregateReferenceSetProperty<*> -> {
							val ids = property.map { DtoUtils.idToString(it)!! }
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
					logger.trace(
						"${indent}dto.relation[{}]: {} = fromRelation[{} ({})]",
						rel.targetRelation,
						dto.getRelation(rel.targetRelation),
						property,
						property.javaClass.simpleName,
					)
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
	private fun fromFields(
		entity: EntityWithProperties,
		dto: JsonDto,
		fields: Collection<FieldConfig>,
	) {
		val indent = if (entity is Part<*>) ".     " else ".   "
		for (fieldConfig in fields) {
			try {
				val fieldName = fieldConfig.targetField
				if (fieldConfig.outgoing != null) {
					dto[fieldName] = fieldConfig.outgoing.invoke(entity)
					logger.trace(
						"${indent}dto[{}]: {} = fromField.outgoing",
						fieldName,
						dto[fieldName],
					)
				} else if (fieldConfig.sourceProperty != null) {
					val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)
					dto[fieldName] = fromProperty(property, fieldConfig.doInline)
					logger.trace(
						"${indent}dto[{}]: {} = fromField[{} ({})]",
						fieldName,
						dto[fieldName],
						property,
						property.javaClass.simpleName,
					)
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
		val properties = aggregate.properties.filter { !config.isExcluded(it) && it.isWritable }
		logger.trace("toAggregate: {}", aggregate)
		logger.trace(". properties: {}", properties.map { it.name })
		logger.trace(". dto: {}", dto)
		toEntity(dto, aggregate, properties)
		toFields(dto, aggregate, config.fields.values)
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
		dto: JsonDto,
		part: Part<*>,
	) {
		part as EntityWithProperties
		val partConfig = config.partAdapterConfig(part.javaClass)
		val properties = part.properties.filter { !partConfig.isExcluded(it) && it.isWritable }
		logger.trace(". toPart: {}", part)
		logger.trace(".   properties: {}", properties.map { it.name })
		logger.trace(".   dto: {}", dto)
		toEntity(dto, part, properties)
		toFields(dto, part, partConfig.fields.values)
	}

	/** Apply DTO values to an aggregate. */
	fun toEntity(
		dto: JsonDto,
		entity: EntityWithProperties,
		properties: List<Property<*>>,
	) {
		val indent = if (entity is Part<*>) ".     " else ".   "
		for (property in properties) {
			if (!dto.containsKey(property.name)) continue
			val dtoValue = dto[property.name]
			try {
				logger.trace(
					"${indent}toEntity.before[{}]({}) = toProperty(dto[{}]: {})",
					property,
					property.javaClass.simpleName,
					property.name,
					dtoValue,
				)
				toProperty(property, dtoValue)
				logger.trace("${indent}toEntity.after[{}]", property)
			} catch (ex: Exception) {
				throw RuntimeException(
					"toEntity(${entity.javaClass.simpleName}.${property.name}) crashed: ${ex.message}",
					ex,
				)
			}
		}
	}

	/** Apply a DTO value to a property */
	@Suppress("UNCHECKED_CAST")
	private fun toProperty(
		property: Property<*>,
		dtoValue: Any?,
		doInline: Boolean = false,
	) {
		when (property) {
			is AggregateReferenceProperty<*> -> {
				property.id = DtoUtils.idFromString(enumId(dtoValue))
			}

			is AggregateReferenceSetProperty<*> -> {
				toReferenceSet(dtoValue, property)
			}

			is PartReferenceProperty<*, *> -> {
				if (doInline) {
					val dtoPart = dtoValue as Map<String, Any?>?
					if (dtoPart != null) {
						check(property.value != null) { "Cannot inline to null part property ${property.name}" }
						toPart(MapDto(dtoPart), property.value!!)
					}
				} else {
					property.id = (dtoValue as String?)?.toInt()
				}
			}

			is PartListProperty<*, *> -> {
				toPartList(dtoValue, property)
			}

			is PartMapProperty<*, *> -> {
				toPartMap(dtoValue, property)
			}

			is EnumProperty<*> -> {
				property.id = enumId(dtoValue)
			}

			is EnumSetProperty<*> -> {
				toEnumSet(dtoValue, property as EnumSetProperty<Enumerated>)
			}

			is BaseProperty<*> -> {
				(property as BaseProperty<Any>).value = toDomainValue(dtoValue, property.type)
			}

			else -> {
				throw IllegalArgumentException(
					"Unsupported property type: ${property.javaClass.name}",
				)
			}
		}
	}

	/**
	 * Apply field values from DTO to entity based on registered field mappings. Uses intelligent type
	 * detection for simple mappings.
	 */
	private fun toFields(
		dto: JsonDto,
		entity: EntityWithProperties,
		fields: Collection<FieldConfig>,
	) {
		val indent = if (entity is Part<*>) ".     " else ".   "
		for (fieldConfig in fields) {
			if (!dto.containsKey(fieldConfig.targetField)) continue
			val fieldName = fieldConfig.targetField
			val dtoValue = dto[fieldName]
			try {
				if (fieldConfig.incoming != null) {
					logger.trace("${indent}toFields.before[{}] = toProperty(dto[{}]: {})", entity, fieldName, dtoValue)
					fieldConfig.incoming.invoke(dtoValue, entity)
					logger.trace("${indent}toFields.after[{}]", entity)
				} else if (fieldConfig.sourceProperty != null) {
					val property = entity.getProperty(fieldConfig.sourceProperty, Any::class)
					logger.trace(
						"${indent}toFields.before[{}]({}) = toProperty(dto[{}]: {})",
						property,
						property.javaClass.simpleName,
						fieldName,
						dtoValue,
					)
					toProperty(property, dtoValue, fieldConfig.doInline)
					logger.trace("${indent}toFields.after[{}]", property)
				}
			} catch (ex: Exception) {
				throw RuntimeException(
					"toField(${entity.javaClass.simpleName}.${fieldConfig.targetField}) crashed: ${ex.message}",
					ex,
				)
			}
		}
	}

	/** Deserialize a reference set from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toReferenceSet(
		dtoValue: Any?,
		property: AggregateReferenceSetProperty<*>,
	) {
		val dtoRefs = dtoValue as List<*>? ?: return
		property.clear()
		for (dtoRef in dtoRefs) {
			val refId = enumId(dtoRef)!!
			property.add(DtoUtils.idFromString(refId)!!)
		}
	}

	/** Deserialize a part list from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toPartList(
		dtoValue: Any?,
		property: PartListProperty<*, *>,
	) {
		val indent = if (property.entity is Part<*>) ".       " else ".     "
		val dtoParts = dtoValue as List<Map<String, Any?>>? ?: return
		val dtoPartsToUpdate = dtoParts.filter { dtoPart ->
			val partId = (dtoPart["id"] as String?)?.toInt()
			partId != null && property.any { it.id == partId }
		}
		val dtoPartsToInsert = dtoParts.filter { dtoPart ->
			val partId = (dtoPart["id"] as String?)?.toInt()
			partId == null || property.none { it.id == partId }
		}
		val partsToDelete = property.filter { part ->
			dtoPartsToUpdate.none { (it["id"] as String).toInt() == part.id }
		}
		logger.trace("{}toPartList({})", indent, property.name)
		logger.trace("{}  toUpdate: {}", indent, dtoPartsToUpdate.map { it["id"] })
		logger.trace("{}  toInsert: {}", indent, dtoPartsToInsert.map { it["id"] })
		logger.trace("{}  toDelete: {}", indent, partsToDelete.map { it.id })
		for (part in partsToDelete) {
			property.remove(part.id)
		}
		for (dtoPart in dtoPartsToUpdate) {
			val partId = (dtoPart["id"] as String).toInt()
			val part = property.getById(partId)
			toPart(MapDto(dtoPart), part)
		}
		for (dtoPart in dtoPartsToInsert) {
			val part = property.add()
			toPart(MapDto(dtoPart), part)
		}
	}

	/** Deserialize a part map from DTO. */
	@Suppress("UNCHECKED_CAST")
	private fun toPartMap(
		dtoValue: Any?,
		property: PartMapProperty<*, *>,
	) {
		val dtoParts = dtoValue as Map<String, Map<String, Any?>>? ?: return
		// TODO optimize updates/inserts/deletes as in toPartList
		property.clear()
		for ((key, dtoPart) in dtoParts) {
			val partId = (dtoPart["id"] as String).toInt()
			val part = property.add(key, partId)
			toPart(MapDto(dtoPart), part)
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
		dtoValue: Any?,
		property: EnumSetProperty<Enumerated>,
	) {
		val dtoEnums = dtoValue as List<*>? ?: return
		property.clear()
		for (dtoEnum in dtoEnums) {
			val enumId = enumId(dtoEnum)!!
			property.add(property.enumeration.getItem(enumId))
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

	fun enumId(
		dto: JsonDto,
		fieldName: String,
	): String? = enumId(dto[fieldName])

	@Suppress("UNCHECKED_CAST")
	fun enumId(dtoValue: Any?): String? =
		when (dtoValue) {
			null -> null
			is EnumeratedDto -> dtoValue.id
			is Map<*, *> -> (dtoValue as Map<String, Any?>)["id"] as String?
			else -> null
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
				value as T?
			}
		}
	}

}
