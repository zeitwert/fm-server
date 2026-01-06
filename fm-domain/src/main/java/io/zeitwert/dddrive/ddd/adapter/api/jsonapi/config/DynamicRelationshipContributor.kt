package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config

import io.crnk.core.engine.information.contributor.ResourceFieldContributor
import io.crnk.core.engine.information.contributor.ResourceFieldContributorContext
import io.crnk.core.engine.information.resource.ResourceField
import io.crnk.core.engine.information.resource.ResourceFieldAccessor
import io.crnk.core.engine.information.resource.ResourceFieldType
import io.crnk.core.resource.annotations.LookupIncludeBehavior
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase
import org.springframework.stereotype.Component

@Component
class DynamicRelationshipContributor : ResourceFieldContributor {

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
			fieldBuilder.type(targetDtoClass)
			fieldBuilder.serializeType(SerializeType.LAZY)
			fieldBuilder.lookupIncludeBehavior(LookupIncludeBehavior.AUTOMATICALLY_WHEN_NULL)

			// Main accessor returns null - let Crnk handle lookup
			fieldBuilder.accessor(NullAccessor(targetDtoClass))

			// ID accessor reads/writes the ID from the relations map
			fieldBuilder.idName("${relationName}Id")
			fieldBuilder.idType(String::class.java)
			fieldBuilder.idAccessor(RelationIdAccessor(relationName))

			fieldBuilder.build()
		}
	}

}

// Always returns null - Crnk will use the ID to look up the related resource
class NullAccessor(
	private val fieldType: Class<*>,
) : ResourceFieldAccessor {

	override fun getImplementationClass(): Class<*> = fieldType

	override fun getValue(resource: Any): Any? = null

	override fun setValue(
		resource: Any,
		fieldValue: Any?,
	) { // no-op or store if needed
	}

}

// Reads/writes the relationship ID from the relations map
class RelationIdAccessor(
	private val relationName: String,
) : ResourceFieldAccessor {

	override fun getImplementationClass(): Class<*> = String::class.java

	override fun getValue(resource: Any): Any? = (resource as AggregateDtoBase<*>).getRelation(relationName)

	override fun setValue(
		resource: Any,
		fieldValue: Any?,
	) {
		(resource as AggregateDtoBase<*>).setRelation(relationName, fieldValue)
	}

}
