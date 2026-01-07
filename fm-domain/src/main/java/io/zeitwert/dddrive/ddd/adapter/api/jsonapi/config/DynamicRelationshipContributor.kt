package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config

import io.crnk.core.engine.information.contributor.ResourceFieldContributor
import io.crnk.core.engine.information.contributor.ResourceFieldContributorContext
import io.crnk.core.engine.information.resource.ResourceField
import io.crnk.core.engine.information.resource.ResourceFieldAccessor
import io.crnk.core.engine.information.resource.ResourceFieldType
import io.crnk.core.resource.annotations.JsonIncludeStrategy
import io.crnk.core.resource.annotations.LookupIncludeBehavior
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DynamicRelationshipContributor : ResourceFieldContributor {

	companion object {

		val logger: Logger = LoggerFactory.getLogger(DynamicRelationshipContributor::class.java)!!
	}

	override fun getResourceFields(context: ResourceFieldContributorContext): List<ResourceField> {
		val resourceType = context.resourceInformation.resourceType
		val entry = ResourceRegistry.byResourceType(resourceType) ?: return emptyList()
		return entry.relationships.map { rel ->

			val targetEntry = ResourceRegistry.byResourceType(rel.resourceType)
			val relationName = rel.targetRelation.removeSuffix("Id")
			val targetDtoClass = targetEntry?.dtoClass ?: Any::class.java

			val fieldBuilder = context.informationBuilder.createResourceField()

			fieldBuilder.name(relationName)
			fieldBuilder.fieldType(ResourceFieldType.RELATIONSHIP)
			fieldBuilder.oppositeResourceType(rel.resourceType)
			fieldBuilder.serializeType(SerializeType.LAZY)
			fieldBuilder.lookupIncludeBehavior(LookupIncludeBehavior.AUTOMATICALLY_WHEN_NULL)
			fieldBuilder.jsonIncludeStrategy(JsonIncludeStrategy.NOT_NULL)

			if (rel.isMultiple) {
				// To-many relationship: type is List, idType is List<String>
				fieldBuilder.type(List::class.java)
				fieldBuilder.idName("${relationName}Ids")
				fieldBuilder.idType(List::class.java)
				fieldBuilder.accessor(NullAccessor(resourceType, relationName, List::class.java, true))
				fieldBuilder.idAccessor(RelationIdAccessor(resourceType, relationName, true))
				logger.debug("DRC[$resourceType]: $relationName -> List<${targetDtoClass.simpleName}> (toMany)")
				logger.debug("DRC[$resourceType]: $relationName.ids -> ${relationName}Ids")
			} else {
				// To-one relationship: type is the target DTO class, idType is String
				fieldBuilder.type(targetDtoClass)
				fieldBuilder.idName("${relationName}Id")
				fieldBuilder.idType(String::class.java)
				fieldBuilder.accessor(NullAccessor(resourceType, relationName, targetDtoClass, false))
				fieldBuilder.idAccessor(RelationIdAccessor(resourceType, relationName, false))
				logger.debug("DRC[$resourceType]: $relationName -> ${targetDtoClass.simpleName} (toOne)")
				logger.debug("DRC[$resourceType]: $relationName.id -> ${relationName}Id")
			}

			fieldBuilder.build()
		}
	}

}

// Always returns null - let Crnk handle lookup
class NullAccessor(
	private val resourceType: String,
	private val relationName: String,
	private val fieldType: Class<*>,
	private val isMultiple: Boolean,
) : ResourceFieldAccessor {

	override fun getImplementationClass(): Class<*> = fieldType

	override fun getValue(resource: Any): Any? {
		resource as AggregateDtoBase<*>
		DynamicRelationshipContributor.logger.trace(
			"DRC[{}({}).{}].getValue()",
			resourceType,
			resource.id,
			relationName,
		)
		return null
	}

	override fun setValue(
		resource: Any,
		fieldValue: Any?,
	) {
		resource as AggregateDtoBase<*>
		DynamicRelationshipContributor.logger.trace(
			"DRC[{}({}).{}].setValue({}, {})",
			resourceType,
			resource.id,
			relationName,
			fieldValue?.javaClass?.simpleName,
			fieldValue,
		)
	}

}

// Reads/writes the relationship ID(s) from the relations map
class RelationIdAccessor(
	private val resourceType: String,
	private val relationName: String,
	private val isMultiple: Boolean,
) : ResourceFieldAccessor {

	override fun getImplementationClass(): Class<*> = if (isMultiple) List::class.java else String::class.java

	override fun getValue(resource: Any): Any? {
		resource as AggregateDtoBase<*>
		val value = resource.getRelation(relationName)
		DynamicRelationshipContributor.logger.trace(
			"DRC[{}({}).{}].getId(): {}",
			resourceType,
			resource.id,
			relationName,
			value,
		)
		return value
	}

	override fun setValue(
		resource: Any,
		fieldValue: Any?,
	) {
		resource as AggregateDtoBase<*>
		DynamicRelationshipContributor.logger.trace(
			"DRC[{}({}).{}].setId({}, {})",
			resourceType,
			resource.id,
			relationName,
			fieldValue?.javaClass?.simpleName,
			fieldValue,
		)
		resource.setRelation(relationName, fieldValue)
	}

}
