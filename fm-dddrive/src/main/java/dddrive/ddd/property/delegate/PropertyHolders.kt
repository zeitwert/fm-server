package dddrive.ddd.property.delegate

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.Part
import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.Enumeration
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.ReferenceSetProperty
import dddrive.ddd.property.model.impl.EnumSetPropertyImpl
import dddrive.ddd.property.model.impl.PartListPropertyImpl
import dddrive.ddd.property.model.impl.ReferenceSetPropertyImpl

/**
 * Creates an EnumSetProperty.
 *
 * Usage: `val labelSet: EnumSetProperty<CodeLabel> = enumSetProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified E : Enumerated> enumSetProperty(
	entity: EntityWithProperties,
	name: String,
): EnumSetProperty<E> =
	synchronized(entity) {
		if (entity.hasProperty(name)) {
			entity.getProperty(name, Any::class) as EnumSetProperty<E>
		}
		val enumeration: Enumeration<E> = (entity as EntityWithPropertiesSPI).directory.getEnumeration(E::class.java)
		val property: EnumSetProperty<E> = EnumSetPropertyImpl(entity, name, enumeration)
		entity.addProperty(property)
		return property
	}

/**
 * Creates an AggregateReferenceSetProperty.
 *
 * Usage: `val userSet: ReferenceSetProperty<ObjUser> = referenceSetProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Aggregate> referenceSetProperty(
	entity: EntityWithProperties,
	name: String,
): ReferenceSetProperty<A> =
	synchronized(entity) {
		if (entity.hasProperty(name)) {
			entity.getProperty(name, Any::class) as ReferenceSetProperty<A>
		}
		val repo: AggregateRepository<A> = (entity as EntityWithPropertiesSPI).directory.getRepository(A::class.java)
		val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(entity, name, repo)
		entity.addProperty(property)
		property
	}

/**
 * Creates a PartListProperty.
 *
 * Usage: `val memberList: PartListProperty<ObjHouseholdPartMember> = partListProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified P : Part<*>> partListProperty(
	entity: EntityWithProperties,
	name: String,
): PartListProperty<P> =
	synchronized(entity) {
		if (entity.hasProperty(name)) {
			entity.getProperty(name, Any::class) as PartListProperty<P>
		}
		val property: PartListProperty<P> = PartListPropertyImpl(entity, name, P::class.java)
		(entity as EntityWithPropertiesSPI).addProperty(property)
		return property
	}
