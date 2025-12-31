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
import dddrive.ddd.property.model.PartMapProperty
import dddrive.ddd.property.model.ReferenceSetProperty
import dddrive.ddd.property.model.impl.EnumSetPropertyImpl
import dddrive.ddd.property.model.impl.PartListPropertyImpl
import dddrive.ddd.property.model.impl.PartMapPropertyImpl
import dddrive.ddd.property.model.impl.ReferenceSetPropertyImpl

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
): ReferenceSetProperty<A> =
	synchronized(this) {
		if (hasProperty(name)) {
			getProperty(name, Any::class) as ReferenceSetProperty<A>
		}
		val repo: AggregateRepository<A> = (this as EntityWithPropertiesSPI).directory.getRepository(A::class.java)
		val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(this, name, repo)
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
