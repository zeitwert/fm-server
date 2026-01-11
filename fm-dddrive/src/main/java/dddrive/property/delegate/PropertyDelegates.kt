package dddrive.property.delegate

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Part
import dddrive.property.model.AggregateReferenceProperty
import dddrive.property.model.BaseProperty
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EnumProperty
import dddrive.property.model.PartReferenceProperty
import dddrive.property.model.impl.AggregateReferencePropertyImpl
import dddrive.property.model.impl.BasePropertyImpl
import dddrive.property.model.impl.EnumPropertyImpl
import dddrive.property.model.impl.PartReferencePropertyImpl
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a property delegate for simple base properties.
 *
 * Usage: `var name by baseProperty<String>("name")`
 *
 * For computed properties, provide a calculator function: Usage: `var memberCount by
 * baseProperty<Int>("memberCount") { memberList.size }`
 */
inline fun <reified T : Any> EntityWithProperties.baseProperty(
	name: String,
	noinline calculator: ((BaseProperty<T>) -> T?)? = null,
): BasePropertyDelegate<T> = BasePropertyDelegate(this, T::class.java, name, calculator)

/**
 * Creates a property delegate for enum properties.
 *
 * Usage: `var status: CodeStatus? by enumProperty("status")`
 *
 * For computed properties, provide an ID calculator function (returns String?): Usage: `var
 * computedStatus by enumProperty<CodeStatus>("computedStatus") { "active" }`
 */
inline fun <reified E : Enumerated> EntityWithProperties.enumProperty(
	name: String,
	noinline idCalculator: ((EnumProperty<E>) -> String?)? = null,
): EnumPropertyDelegate<E> = EnumPropertyDelegate(this, E::class.java, name, idCalculator)

/**
 * Creates a property delegate for aggregate reference properties.
 *
 * Usage: `var owner: ObjUser? by referenceProperty("owner")`
 *
 * For computed properties, provide an ID calculator function (returns Any?): Usage: `var
 * computedOwner by referenceProperty<ObjUser>("computedOwner") { someUserId }`
 */
inline fun <reified A : Aggregate> EntityWithProperties.referenceProperty(
	name: String,
	noinline idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
): ReferencePropertyDelegate<A> = ReferencePropertyDelegate(this, A::class.java, name, idCalculator)

/**
 * Creates a property delegate for aggregate reference ID properties.
 *
 * Usage: `var ownerId by referenceIdProperty<ObjUser>("owner")`
 *
 * For computed properties, provide an ID calculator function (returns Any?): Usage: `var
 * computedOwnerId by referenceIdProperty<ObjUser>("computedOwner") { someUserId }`
 */
inline fun <reified A : Aggregate> EntityWithProperties.referenceIdProperty(
	name: String,
	noinline idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
): ReferenceIdPropertyDelegate<A> = ReferenceIdPropertyDelegate(this, A::class.java, name, idCalculator)

/**
 * Creates a property delegate for part reference properties.
 *
 * Usage: `var mainMember: ObjHouseholdPartMember? by partReferenceProperty("mainMember")`
 *
 * For computed properties, provide an ID calculator function (returns Int?): Usage: `var
 * firstMember by partReferenceProperty<ObjHousehold, ObjHouseholdPartMember>("firstMember") {
 * memberList.firstOrNull()?.id }`
 */
inline fun <reified A : Aggregate, reified P : Part<A>> EntityWithProperties.partReferenceProperty(
	name: String,
	noinline idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
): PartReferencePropertyDelegate<A, P> =
	PartReferencePropertyDelegate(this, A::class.java, P::class.java, name, idCalculator)

/**
 * Creates a property delegate for part reference ID properties.
 *
 * Usage: `var mainMemberId: Int? by partReferenceIdProperty<ObjHousehold,
 * ObjHouseholdPartMember>("mainMember")`
 *
 * For computed properties, provide an ID calculator function (returns Int?): Usage: `var
 * firstMemberId by partReferenceIdProperty<ObjHousehold, ObjHouseholdPartMember>("firstMember") {
 * memberList.firstOrNull()?.id }`
 */
inline fun <reified A : Aggregate, reified P : Part<A>> EntityWithProperties.partReferenceIdProperty(
	name: String,
	noinline idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
): PartReferenceIdPropertyDelegate<A, P> =
	PartReferenceIdPropertyDelegate(this, A::class.java, P::class.java, name, idCalculator)

class BasePropertyDelegate<T : Any>(
	entity: EntityWithProperties,
	type: Class<T>,
	name: String,
	calculator: ((BaseProperty<T>) -> T?)? = null,
) : ReadWriteProperty<EntityWithProperties, T?> {

	@Volatile
	private var property: BaseProperty<T> = getOrAddProperty(entity, name, type, calculator)

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
		calculator: ((BaseProperty<T>) -> T?)?,
	): BaseProperty<T> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as BaseProperty<T>
			} else {
				entity.getOrAddBaseProperty(name, type, calculator)
			}
		}

}

class EnumPropertyDelegate<E : Enumerated>(
	entity: EntityWithProperties,
	enumType: Class<E>,
	name: String,
	idCalculator: ((EnumProperty<E>) -> String?)? = null,
) : ReadWriteProperty<EntityWithProperties, E?> {

	@Volatile
	private var property: EnumProperty<E> = getOrAddProperty(entity, name, enumType, idCalculator)

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
		idCalculator: ((EnumProperty<E>) -> String?)?,
	): EnumProperty<E> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as EnumProperty<E>
			} else {
				entity.getOrAddEnumProperty(name, enumType, idCalculator)
			}
		}

}

class ReferencePropertyDelegate<A : Aggregate>(
	entity: EntityWithProperties,
	aggregateType: Class<A>,
	name: String,
	idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
) : ReadWriteProperty<EntityWithProperties, A?> {

	@Volatile
	private var property: AggregateReferenceProperty<A> = getOrAddProperty(entity, name, aggregateType, idCalculator)

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
		idCalculator: ((AggregateReferenceProperty<A>) -> Any?)?,
	): AggregateReferenceProperty<A> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as AggregateReferenceProperty<A>
			} else {
				entity.getOrAddReferenceProperty(name, aggregateType, idCalculator)
			}
		}

}

class ReferenceIdPropertyDelegate<A : Aggregate>(
	entity: EntityWithProperties,
	aggregateType: Class<A>,
	name: String,
	idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
) : ReadWriteProperty<EntityWithProperties, Any?> {

	@Volatile
	private var property: AggregateReferenceProperty<A> = getOrAddProperty(entity, name, aggregateType, idCalculator)

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
		idCalculator: ((AggregateReferenceProperty<A>) -> Any?)?,
	): AggregateReferenceProperty<A> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as AggregateReferenceProperty<A>
			} else {
				entity.getOrAddReferenceProperty(name, aggregateType, idCalculator)
			}
		}

}

class PartReferencePropertyDelegate<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	aggrType: Class<A>,
	partType: Class<P>,
	name: String,
	idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
) : ReadWriteProperty<EntityWithProperties, P?> {

	@Volatile
	private var property: PartReferenceProperty<A, P> = getOrAddProperty(entity, partType, name, idCalculator)

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
		idCalculator: ((PartReferenceProperty<A, P>) -> Int?)?,
	): PartReferenceProperty<A, P> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as PartReferenceProperty<A, P>
			} else {
				entity.getOrAddPartReferenceProperty(name, partType, idCalculator)
			}
		}

}

class PartReferenceIdPropertyDelegate<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	aggrType: Class<A>,
	partType: Class<P>,
	name: String,
	idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
) : ReadWriteProperty<EntityWithProperties, Int?> {

	@Volatile
	private var property: PartReferenceProperty<A, P> = getOrAddProperty(entity, partType, name, idCalculator)

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
		idCalculator: ((PartReferenceProperty<A, P>) -> Int?)?,
	): PartReferenceProperty<A, P> =
		synchronized(entity) {
			if (entity.hasProperty(name)) {
				entity.getProperty(name, Any::class) as PartReferenceProperty<A, P>
			} else {
				entity.getOrAddPartReferenceProperty(name, partType, idCalculator)
			}
		}

}

/**
 * Gets an existing base property or creates a new one if it doesn't exist. Used by property
 * delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any> EntityWithProperties.getOrAddBaseProperty(
	name: String,
	type: Class<T>,
	calculator: ((BaseProperty<T>) -> T?)? = null,
): BaseProperty<T> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as BaseProperty<T>
	}
	val property = BasePropertyImpl(this, name, type, calculator)
	addProperty(property)
	return property
}

/**
 * Gets an existing enum property or creates a new one if it doesn't exist. Used by property
 * delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <E : Enumerated> EntityWithProperties.getOrAddEnumProperty(
	name: String,
	enumType: Class<E>,
	idCalculator: ((EnumProperty<E>) -> String?)? = null,
): EnumProperty<E> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as EnumProperty<E>
	}
	val property = EnumPropertyImpl(this, name, enumType, idCalculator)
	addProperty(property)
	return property
}

/**
 * Gets an existing aggregate reference property or creates a new one if it doesn't exist. Used by
 * property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <A : Aggregate> EntityWithProperties.getOrAddReferenceProperty(
	name: String,
	aggregateType: Class<A>,
	idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
): AggregateReferenceProperty<A> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as AggregateReferenceProperty<A>
	}
	val property = AggregateReferencePropertyImpl(this, name, aggregateType, idCalculator)
	addProperty(property)
	return property
}

/**
 * Gets an existing part reference property or creates a new one if it doesn't exist. Used by
 * property delegates for lazy property registration.
 */
@Suppress("UNCHECKED_CAST")
private fun <A : Aggregate, P : Part<A>> EntityWithProperties.getOrAddPartReferenceProperty(
	name: String,
	partType: Class<P>,
	idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
): PartReferenceProperty<A, P> {
	if (hasProperty(name)) {
		return getProperty(name, Any::class) as PartReferenceProperty<A, P>
	}
	val property = PartReferencePropertyImpl(this, name, partType, idCalculator)
	addProperty(property)
	return property
}
