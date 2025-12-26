package io.dddrive.property.delegate

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.Part
import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.ReferenceSetProperty
import io.dddrive.property.model.base.EntityWithPropertiesBase
import io.dddrive.property.model.impl.EnumSetPropertyImpl
import io.dddrive.property.model.impl.PartListPropertyImpl
import io.dddrive.property.model.impl.ReferenceSetPropertyImpl

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
		(entity as EntityWithPropertiesBase).addProperty(property)
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
		(entity as EntityWithPropertiesBase).addProperty(property)
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
		(entity as EntityWithPropertiesBase).addProperty(property)
		return property
	}
