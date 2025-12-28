package dddrive.ddd.property.delegate

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.EnumProperty
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.impl.AggregateReferencePropertyImpl
import dddrive.ddd.property.model.impl.BasePropertyImpl
import dddrive.ddd.property.model.impl.EnumPropertyImpl
import dddrive.ddd.property.model.impl.PartReferencePropertyImpl
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a property delegate for simple base properties.
 *
 * Usage: `var name: String? by baseProperty()`
 */
inline fun <reified T : Any> baseProperty(
	entity: EntityWithProperties,
	name: String,
): BasePropertyDelegate<T> = BasePropertyDelegate(entity, T::class.java, name)

/**
 * Creates a property delegate for enum properties.
 *
 * Usage: `var status: CodeStatus? by enumProperty()`
 */
inline fun <reified E : Enumerated> enumProperty(
	entity: EntityWithProperties,
	name: String,
): EnumPropertyDelegate<E> = EnumPropertyDelegate(entity, E::class.java, name)

/**
 * Creates a property delegate for aggregate reference properties.
 *
 * Usage: `var owner: ObjUser? by referenceProperty()`
 */
inline fun <reified A : Aggregate> referenceProperty(
	entity: EntityWithProperties,
	name: String,
): ReferencePropertyDelegate<A> = ReferencePropertyDelegate(entity, A::class.java, name)

/**
 * Creates a property delegate for aggregate reference ID properties.
 *
 * Usage: `var ownerId: Any? by referenceIdProperty<ObjUser>()`
 */
inline fun <reified A : Aggregate> referenceIdProperty(
	entity: EntityWithProperties,
	name: String,
): ReferenceIdPropertyDelegate<A> = ReferenceIdPropertyDelegate(entity, A::class.java, name)

/**
 * Creates a property delegate for part reference properties.
 *
 * Usage: `var mainMember: ObjHouseholdPartMember? by partReferenceProperty()`
 */
inline fun <reified A : Aggregate, reified P : Part<A>> partReferenceProperty(
	entity: EntityWithProperties,
	name: String,
): PartReferencePropertyDelegate<A, P> = PartReferencePropertyDelegate(entity, A::class.java, P::class.java, name)

/**
 * Creates a property delegate for part reference ID properties.
 *
 * Usage: `var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()`
 */
inline fun <reified A : Aggregate, reified P : Part<A>> partReferenceIdProperty(
	entity: EntityWithProperties,
	name: String,
): PartReferenceIdPropertyDelegate<A, P> = PartReferenceIdPropertyDelegate(entity, A::class.java, P::class.java, name)

class BasePropertyDelegate<T : Any>(
	entity: EntityWithProperties,
	type: Class<T>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, T?> {

	@Volatile
	private var property: BaseProperty<T> = getOrAddProperty(entity, name, type)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): T? = this.property.value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: T?,
	) {
		this.property.value = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		type: Class<T>,
	): BaseProperty<T> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as BaseProperty<T>
			} else {
				entity.getOrAddBaseProperty(name, type)
			}
		}

}

class EnumPropertyDelegate<E : Enumerated>(
	entity: EntityWithProperties,
	enumType: Class<E>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, E?> {

	@Volatile
	private var property: EnumProperty<E> = getOrAddProperty(entity, name, enumType)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): E? = this.property.value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: E?,
	) {
		this.property.value = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		enumType: Class<E>,
	): EnumProperty<E> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as EnumProperty<E>
			} else {
				entity.getOrAddEnumProperty(name, enumType)
			}
		}

}

class ReferencePropertyDelegate<A : Aggregate>(
	entity: EntityWithProperties,
	aggregateType: Class<A>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, A?> {

	@Volatile
	private var property: AggregateReferenceProperty<A> = getOrAddProperty(entity, name, aggregateType)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): A? = this.property.value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: A?,
	) {
		this.property.value = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		aggregateType: Class<A>,
	): AggregateReferenceProperty<A> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as AggregateReferenceProperty<A>
			} else {
				entity.getOrAddReferenceProperty(name, aggregateType)
			}
		}

}

class ReferenceIdPropertyDelegate<A : Aggregate>(
	entity: EntityWithProperties,
	aggregateType: Class<A>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, Any?> {

	@Volatile
	private var property: AggregateReferenceProperty<A> = getOrAddProperty(entity, name, aggregateType)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): Any? = this.property.id

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: Any?,
	) {
		this.property.id = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		name: String,
		aggregateType: Class<A>,
	): AggregateReferenceProperty<A> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as AggregateReferenceProperty<A>
			} else {
				entity.getOrAddReferenceProperty(name, aggregateType)
			}
		}

}

class PartReferencePropertyDelegate<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	aggrType: Class<A>,
	partType: Class<P>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, P?> {

	@Volatile
	private var property: PartReferenceProperty<A, P> = getOrAddProperty(entity, partType, name)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): P? = this.property.value

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: P?,
	) {
		this.property.value = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		partType: Class<P>,
		name: String,
	): PartReferenceProperty<A, P> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as PartReferenceProperty<A, P>
			} else {
				entity.getOrAddPartReferenceProperty(name, partType)
			}
		}

}

class PartReferenceIdPropertyDelegate<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	aggrType: Class<A>,
	partType: Class<P>,
	name: String,
) : ReadWriteProperty<EntityWithProperties, Int?> {

	@Volatile
	private var property: PartReferenceProperty<A, P> = getOrAddProperty(entity, partType, name)

	override fun getValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
	): Int? = this.property.id

	override fun setValue(
		thisRef: EntityWithProperties,
		property: KProperty<*>,
		value: Int?,
	) {
		this.property.id = value
	}

	@Suppress("UNCHECKED_CAST")
	private fun getOrAddProperty(
		entity: EntityWithProperties,
		partType: Class<P>,
		name: String,
	): PartReferenceProperty<A, P> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as PartReferenceProperty<A, P>
			} else {
				entity.getOrAddPartReferenceProperty(name, partType)
			}
		}

}

/**
 * Gets an existing base property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any> EntityWithProperties.getOrAddBaseProperty(
	name: String,
	type: Class<T>,
): BaseProperty<T> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as BaseProperty<T>
	}
	val property: BaseProperty<T> = BasePropertyImpl(this, name, type)
	(this as EntityWithPropertiesSPI).addProperty(property)
	return property
}

/**
 * Gets an existing enum property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <E : Enumerated> EntityWithProperties.getOrAddEnumProperty(
	name: String,
	enumType: Class<E>,
): EnumProperty<E> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as EnumProperty<E>
	}
	val property: EnumProperty<E> = EnumPropertyImpl(this, name, enumType)
	(this as EntityWithPropertiesSPI).addProperty(property)
	return property
}

/**
 * Gets an existing aggregate reference property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <A : Aggregate> EntityWithProperties.getOrAddReferenceProperty(
	name: String,
	aggregateType: Class<A>,
): AggregateReferenceProperty<A> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as AggregateReferenceProperty<A>
	}
	val property: AggregateReferenceProperty<A> = AggregateReferencePropertyImpl(this, name, aggregateType)
	(this as EntityWithPropertiesSPI).addProperty(property)
	return property
}

/**
 * Gets an existing part reference property or creates a new one if it doesn't exist.
 * Used by property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <A : Aggregate, P : Part<A>> EntityWithProperties.getOrAddPartReferenceProperty(
	name: String,
	partType: Class<P>,
): PartReferenceProperty<A, P> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as PartReferenceProperty<A, P>
	}
	val property: PartReferenceProperty<A, P> = PartReferencePropertyImpl(this, name, partType)
	(this as EntityWithPropertiesSPI).addProperty(property)
	return property
}
