package dddrive.property.delegate

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregateRepository
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration
import dddrive.ddd.model.Part
import dddrive.property.model.AggregateReferenceSetProperty
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EntityWithPropertiesSPI
import dddrive.property.model.EnumSetProperty
import dddrive.property.model.PartListProperty
import dddrive.property.model.PartMapProperty
import dddrive.property.model.impl.AggregateReferenceSetPropertyImpl
import dddrive.property.model.impl.EnumSetPropertyImpl
import dddrive.property.model.impl.PartListPropertyImpl
import dddrive.property.model.impl.PartMapPropertyImpl

/**
 * Creates an EnumSetProperty.
 *
 * Usage: `val labelSet: EnumSetProperty<CodeLabel> = enumSetProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified E : Enumerated> EntityWithProperties.enumSetProperty(
	name: String,
): EnumSetProperty<E> =
	synchronized(this) {
		if (hasProperty(name)) {
			getProperty(name, Any::class) as EnumSetProperty<E>
		}
		val enumeration: Enumeration<E> = (this as EntityWithPropertiesSPI).directory.getEnumeration(E::class.java)
		val property: EnumSetProperty<E> = EnumSetPropertyImpl(this, name, enumeration)
		addProperty(property)
		return property
	}

/**
 * Creates an AggregateReferenceSetProperty.
 *
 * Usage: `val userSet: ReferenceSetProperty<ObjUser> = referenceSetProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Aggregate> EntityWithProperties.referenceSetProperty(
	name: String,
): AggregateReferenceSetProperty<A> =
	synchronized(this) {
		if (hasProperty(name)) {
			getProperty(name, Any::class) as AggregateReferenceSetProperty<A>
		}
		val repo: AggregateRepository<A> = (this as EntityWithPropertiesSPI).directory.getRepository(A::class.java)
		val property: AggregateReferenceSetProperty<A> = AggregateReferenceSetPropertyImpl(this, name, repo, A::class.java)
		addProperty(property)
		property
	}

/**
 * Creates a PartListProperty.
 *
 * Usage: `val memberList: PartListProperty<ObjHouseholdPartMember> = partListProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Aggregate, reified P : Part<A>> EntityWithProperties.partListProperty(
	name: String,
): PartListProperty<A, P> =
	synchronized(this) {
		if (hasProperty(name)) {
			getProperty(name, Any::class) as PartListProperty<A, P>
		}
		val property: PartListProperty<A, P> = PartListPropertyImpl(this, name, P::class.java)
		addProperty(property)
		return property
	}

/**
 * Creates a PartMapProperty.
 *
 * Usage: `val attributeMap: PartMapProperty<ObjHousehold, ObjHouseholdPartMember> = partMapProperty()`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Aggregate, reified P : Part<A>> EntityWithProperties.partMapProperty(
	name: String,
): PartMapProperty<A, P> =
	synchronized(this) {
		if (hasProperty(name)) {
			getProperty(name, Any::class) as PartMapProperty<A, P>
		}
		val property: PartMapProperty<A, P> = PartMapPropertyImpl(this, name, P::class.java)
		addProperty(property)
		return property
	}
