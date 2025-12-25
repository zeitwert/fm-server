package io.dddrive.property.delegate

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.enums.model.Enumerated
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.ReferenceSetProperty
import io.dddrive.property.model.base.EntityWithPropertiesBase
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
	private val enumType: Class<E>,
) : ReadOnlyProperty<EntityWithProperties, EnumSetProperty<E>> {

	@Volatile
	private var property: EnumSetProperty<E>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): EnumSetProperty<E> {
		var p = this.property
		if (p == null) {
			synchronized(this) {
				p = this.property
				if (p == null) {
					p = (thisRef as EntityWithPropertiesBase).getOrAddEnumSetProperty(property.name, enumType)
					this.property = p
				}
			}
		}
		return p!!
	}
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
	private val aggregateType: Class<A>,
) : ReadOnlyProperty<EntityWithProperties, ReferenceSetProperty<A>> {

	@Volatile
	private var property: ReferenceSetProperty<A>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): ReferenceSetProperty<A> {
		var p = this.property
		if (p == null) {
			synchronized(this) {
				p = this.property
				if (p == null) {
					p =
						(thisRef as EntityWithPropertiesBase).getOrAddReferenceSetProperty(
							property.name,
							aggregateType,
						)
					this.property = p
				}
			}
		}
		return p!!
	}
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
	private val partType: Class<P>,
) : ReadOnlyProperty<EntityWithProperties, PartListProperty<P>> {

	@Volatile
	private var property: PartListProperty<P>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): PartListProperty<P> {
		var p = this.property
		if (p == null) {
			synchronized(this) {
				p = this.property
				if (p == null) {
					p =
						(thisRef as EntityWithPropertiesBase).getOrAddPartListProperty(
							property.name,
							partType,
						)
					this.property = p
				}
			}
		}
		return p!!
	}
}

// ============================================================================
// Reified factory functions for cleaner syntax
// ============================================================================

/**
 * Creates a property holder for enum set properties.
 *
 * Usage: `val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty()`
 */
inline fun <reified E : Enumerated> enumSetProperty(): EnumSetPropertyHolder<E> = EnumSetPropertyHolder(E::class.java)

/**
 * Creates a property holder for aggregate reference set properties.
 *
 * Usage: `val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty()`
 */
inline fun <reified A : Aggregate> referenceSetProperty(): ReferenceSetPropertyHolder<A> = ReferenceSetPropertyHolder(A::class.java)

/**
 * Creates a property holder for part list properties.
 *
 * Usage: `val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty()`
 */
inline fun <reified P : Part<*>> partListProperty(): PartListPropertyHolder<P> = PartListPropertyHolder(P::class.java)
