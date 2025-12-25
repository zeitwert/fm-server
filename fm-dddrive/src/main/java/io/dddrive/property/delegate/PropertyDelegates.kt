package io.dddrive.property.delegate

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.enums.model.Enumerated
import io.dddrive.property.model.AggregateReferenceProperty
import io.dddrive.property.model.BaseProperty
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.PartReferenceProperty
import io.dddrive.property.model.base.EntityWithPropertiesBase
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Kotlin property delegate for simple base properties (String, Int, BigDecimal, etc.).
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var name: String? by baseProperty()
 *     var count: Int? by baseProperty()
 * }
 * ```
 */
class BasePropertyDelegate<T : Any>(
	private val type: Class<T>,
) : ReadWriteProperty<EntityWithProperties, T?> {

	@Volatile
	private var property: BaseProperty<T>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): T? = getOrCreateProperty(thisRef, property).value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: T?,
	) {
		getOrCreateProperty(thisRef, property).value = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): BaseProperty<T> {
		// Double-checked locking for thread safety during initialization
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					p = (entity as EntityWithPropertiesBase).getOrAddBaseProperty(prop.name, type)
					property = p
				}
			}
		}
		return p!!
	}
}

/**
 * Kotlin property delegate for enum properties.
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var status: CodeStatus? by enumProperty()
 * }
 * ```
 */
class EnumPropertyDelegate<E : Enumerated>(
	private val enumType: Class<E>,
) : ReadWriteProperty<EntityWithProperties, E?> {

	@Volatile
	private var property: EnumProperty<E>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): E? = getOrCreateProperty(thisRef, property).value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: E?,
	) {
		getOrCreateProperty(thisRef, property).value = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): EnumProperty<E> {
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					p = (entity as EntityWithPropertiesBase).getOrAddEnumProperty(prop.name, enumType)
					property = p
				}
			}
		}
		return p!!
	}
}

/**
 * Kotlin property delegate for aggregate reference properties.
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var owner: ObjUser? by referenceProperty()
 * }
 * ```
 */
class ReferencePropertyDelegate<A : Aggregate>(
	private val aggregateType: Class<A>,
) : ReadWriteProperty<EntityWithProperties, A?> {

	@Volatile
	private var property: AggregateReferenceProperty<A>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): A? = getOrCreateProperty(thisRef, property).value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: A?,
	) {
		getOrCreateProperty(thisRef, property).value = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): AggregateReferenceProperty<A> {
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					p =
						(entity as EntityWithPropertiesBase).getOrAddReferenceProperty(
							prop.name,
							aggregateType,
						)
					property = p
				}
			}
		}
		return p!!
	}
}

/**
 * Kotlin property delegate for aggregate reference ID properties. Use this when you only need to
 * work with the ID, not the full aggregate.
 *
 * Note that the underlying property name is derived by removing the "Id" suffix from the Kotlin property.
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var ownerId: Any? by referenceIdProperty<ObjUser>()
 * }
 * ```
 */
class ReferenceIdPropertyDelegate<A : Aggregate>(
	private val aggregateType: Class<A>,
) : ReadWriteProperty<EntityWithProperties, Any?> {

	@Volatile
	private var property: AggregateReferenceProperty<A>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): Any? = getOrCreateProperty(thisRef, property).id

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: Any?,
	) {
		getOrCreateProperty(thisRef, property).id = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): AggregateReferenceProperty<A> {
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					// Remove "Id" suffix to get base property name
					val baseName = prop.name.removeSuffix("Id")
					p =
						(entity as EntityWithPropertiesBase).getOrAddReferenceProperty(
							baseName,
							aggregateType,
						)
					property = p
				}
			}
		}
		return p!!
	}
}

/**
 * Kotlin property delegate for part reference properties.
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var mainMember: ObjHouseholdPartMember? by partReferenceProperty()
 * }
 * ```
 */
class PartReferencePropertyDelegate<P : Part<*>>(
	private val partType: Class<P>,
) : ReadWriteProperty<EntityWithProperties, P?> {

	@Volatile
	private var property: PartReferenceProperty<P>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): P? = getOrCreateProperty(thisRef, property).value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: P?,
	) {
		getOrCreateProperty(thisRef, property).value = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): PartReferenceProperty<P> {
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					p =
						(entity as EntityWithPropertiesBase).getOrAddPartReferenceProperty(
							prop.name,
							partType,
						)
					property = p
				}
			}
		}
		return p!!
	}
}

/**
 * Kotlin property delegate for part reference ID properties. Use this when you only need to work
 * with the part ID.
 *
 * Note that the underlying property name is derived by removing the "Id" suffix from the Kotlin property.
 *
 * Usage:
 * ```
 * class MyAggregate : AggregateBase(...) {
 *     var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()
 * }
 * ```
 */
class PartReferenceIdPropertyDelegate<P : Part<*>>(
	private val partType: Class<P>,
) : ReadWriteProperty<EntityWithProperties, Int?> {

	@Volatile
	private var property: PartReferenceProperty<P>? = null

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): Int? = getOrCreateProperty(thisRef, property).id

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: Int?,
	) {
		getOrCreateProperty(thisRef, property).id = value
	}

	private fun getOrCreateProperty(
		entity: EntityWithProperties,
		prop: KProperty<*>,
	): PartReferenceProperty<P> {
		var p = property
		if (p == null) {
			synchronized(this) {
				p = property
				if (p == null) {
					// Remove "Id" suffix to get base property name
					val baseName = prop.name.removeSuffix("Id")
					p = (entity as EntityWithPropertiesBase).getOrAddPartReferenceProperty(baseName, partType)
					property = p
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
 * Creates a property delegate for simple base properties.
 *
 * Usage: `var name: String? by baseProperty()`
 */
inline fun <reified T : Any> baseProperty(): BasePropertyDelegate<T> = BasePropertyDelegate(T::class.java)

/**
 * Creates a property delegate for enum properties.
 *
 * Usage: `var status: CodeStatus? by enumProperty()`
 */
inline fun <reified E : Enumerated> enumProperty(): EnumPropertyDelegate<E> = EnumPropertyDelegate(E::class.java)

/**
 * Creates a property delegate for aggregate reference properties.
 *
 * Usage: `var owner: ObjUser? by referenceProperty()`
 */
inline fun <reified A : Aggregate> referenceProperty(): ReferencePropertyDelegate<A> = ReferencePropertyDelegate(A::class.java)

/**
 * Creates a property delegate for aggregate reference ID properties.
 *
 * Usage: `var ownerId: Any? by referenceIdProperty<ObjUser>()`
 */
inline fun <reified A : Aggregate> referenceIdProperty(): ReferenceIdPropertyDelegate<A> = ReferenceIdPropertyDelegate(A::class.java)

/**
 * Creates a property delegate for part reference properties.
 *
 * Usage: `var mainMember: ObjHouseholdPartMember? by partReferenceProperty()`
 */
inline fun <reified P : Part<*>> partReferenceProperty(): PartReferencePropertyDelegate<P> = PartReferencePropertyDelegate(P::class.java)

/**
 * Creates a property delegate for part reference ID properties.
 *
 * Usage: `var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()`
 */
inline fun <reified P : Part<*>> partReferenceIdProperty(): PartReferenceIdPropertyDelegate<P> = PartReferenceIdPropertyDelegate(P::class.java)
