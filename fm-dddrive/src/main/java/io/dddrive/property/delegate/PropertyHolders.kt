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
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Kotlin property holder for enum set properties. Returns the EnumSetProperty directly (which
 * implements Set<E>).
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty()
 * }
 * ```
 */
class EnumSetPropertyHolder<E : Enumerated>(
	entity: EntityWithProperties,
	enumType: Class<E>,
	name: String,
) : ReadOnlyProperty<EntityWithProperties, EnumSetProperty<E>> {

	@Volatile
	private var property: EnumSetProperty<E> = getOrAddProperty(entity, name, enumType)

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		enumType: Class<E>,
	): EnumSetProperty<E> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as EnumSetProperty<E>
			} else {
				(entity as EntityWithPropertiesBase).getOrAddEnumSetProperty(name, enumType)
			}
		}

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): EnumSetProperty<E> = this.property

}

/**
 * Kotlin property holder for aggregate reference set properties. Returns the ReferenceSetProperty
 * directly (which implements Set<Any>).
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty()
 * }
 * ```
 */
class ReferenceSetPropertyHolder<A : Aggregate>(
	entity: EntityWithProperties,
	aggregateType: Class<A>,
	name: String,
) : ReadOnlyProperty<EntityWithProperties, ReferenceSetProperty<A>> {

	@Volatile
	private var property: ReferenceSetProperty<A> = getOrAddProperty(entity, name, aggregateType)

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		aggregateType: Class<A>,
	): ReferenceSetProperty<A> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as ReferenceSetProperty<A>
			} else {
				(entity as EntityWithPropertiesBase).getOrAddReferenceSetProperty(name, aggregateType)
			}
		}

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): ReferenceSetProperty<A> = this.property

}

/**
 * Kotlin property holder for part list properties. Returns the PartListProperty directly (which
 * implements List<P>).
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty()
 * }
 * ```
 */
class PartListPropertyHolder<P : Part<*>>(
	entity: EntityWithProperties,
	partType: Class<P>,
	name: String,
) : ReadOnlyProperty<EntityWithProperties, PartListProperty<P>> {

	@Volatile
	private var property: PartListProperty<P> = getOrAddProperty(entity, partType, name)

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		partType: Class<P>,
		name: String,
	): PartListProperty<P> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as PartListProperty<P>
			} else {
				(entity as EntityWithPropertiesBase).getOrAddPartListProperty(name, partType)
			}
		}

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): PartListProperty<P> = this.property

}

// ============================================================================
// Reified factory functions for cleaner syntax
// ============================================================================

/**
 * Creates a property holder for enum set properties.
 *
 * Usage: `val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty()`
 */
inline fun <reified E : Enumerated> enumSetProperty(
	entity: EntityWithProperties,
	name: String,
): EnumSetPropertyHolder<E> = EnumSetPropertyHolder(entity, E::class.java, name)

/**
 * Creates a property holder for aggregate reference set properties.
 *
 * Usage: `val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty()`
 */
inline fun <reified A : Aggregate> referenceSetProperty(
	entity: EntityWithProperties,
	name: String,
): ReferenceSetPropertyHolder<A> = ReferenceSetPropertyHolder(entity, A::class.java, name)

/**
 * Creates a property holder for part list properties.
 *
 * Usage: `val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty()`
 */
inline fun <reified P : Part<*>> partListProperty(
	entity: EntityWithProperties,
	name: String,
): PartListPropertyHolder<P> = PartListPropertyHolder(entity, P::class.java, name)

/**
 * Gets an existing enum set property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <E : Enumerated> EntityWithProperties.getOrAddEnumSetProperty(
	name: String,
	enumType: Class<E>,
): EnumSetProperty<E> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as EnumSetProperty<E>
	}
	val enumeration: Enumeration<E> = (this as EntityWithPropertiesSPI).directory.getEnumeration(enumType)
	val property: EnumSetProperty<E> = EnumSetPropertyImpl(this, name, enumeration)
	(this as EntityWithPropertiesBase).addProperty(property)
	return property
}

/**
 * Gets an existing reference set property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <A : Aggregate> EntityWithProperties.getOrAddReferenceSetProperty(
	name: String,
	aggregateType: Class<A>,
): ReferenceSetProperty<A> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as ReferenceSetProperty<A>
	}
	val repo: AggregateRepository<A> = (this as EntityWithPropertiesSPI).directory.getRepository(aggregateType)
	val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(this, name, repo)
	(this as EntityWithPropertiesBase).addProperty(property)
	return property
}

/**
 * Gets an existing part list property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <P : Part<*>> EntityWithProperties.getOrAddPartListProperty(
	name: String,
	partType: Class<P>,
): PartListProperty<P> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as PartListProperty<P>
	}
	val property: PartListProperty<P> = PartListPropertyImpl(this, name, partType)
	(this as EntityWithPropertiesBase).addProperty(property)
	return property
}
