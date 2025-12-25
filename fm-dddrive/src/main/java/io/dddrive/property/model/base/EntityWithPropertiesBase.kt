package io.dddrive.property.model.base

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.Part
import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration
import io.dddrive.property.model.AggregateReferenceProperty
import io.dddrive.property.model.BaseProperty
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.PartReferenceProperty
import io.dddrive.property.model.Property
import io.dddrive.property.model.ReferenceSetProperty
import io.dddrive.property.model.impl.AggregateReferencePropertyImpl
import io.dddrive.property.model.impl.BasePropertyImpl
import io.dddrive.property.model.impl.EnumPropertyImpl
import io.dddrive.property.model.impl.EnumSetPropertyImpl
import io.dddrive.property.model.impl.PartListPropertyImpl
import io.dddrive.property.model.impl.PartReferencePropertyImpl
import io.dddrive.property.model.impl.ReferenceSetPropertyImpl
import kotlin.reflect.KClass

abstract class EntityWithPropertiesBase :
	EntityWithProperties,
	EntityWithPropertiesSPI {

	private val propertyMap: MutableMap<String, Property<*>> = mutableMapOf()
	private val partMap: MutableMap<Int, Part<*>> = mutableMapOf()

	override fun hasProperty(name: String): Boolean = propertyMap.containsKey(name)

	// override fun getProperty(name: String): Property<*> = propertyMap[name]!!

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> {
		require(hasProperty(name)) { "property [$name] not found in ${javaClass.simpleName}" }
		return propertyMap[name]!! as Property<T>
	}

	override val properties: List<Property<*>>
		get() = propertyMap.values.toList()

	override fun hasPart(partId: Int): Boolean = partMap.containsKey(partId)

	override fun getPart(partId: Int): Part<*> = partMap[partId]!!

	protected fun addPart(part: Part<*>) {
		partMap.put(part.id, part)
	}

	fun addProperty(property: Property<*>) {
		require(!hasProperty(property.name)) { "property [" + property.name + "] is unique" }
		propertyMap.put(property.name, property)
	}

	fun <T : Any> addBaseProperty(
		name: String,
		type: Class<T>,
	): BaseProperty<T> {
		val property: BaseProperty<T> = BasePropertyImpl(this, name, type)
		addProperty(property)
		return property
	}

	protected fun <E : Enumerated> addEnumProperty(
		name: String,
		enumType: Class<E>,
	): EnumProperty<E> {
		val enumeration: Enumeration<E> = directory.getEnumeration(enumType)
		val property: EnumProperty<E> = EnumPropertyImpl(this, name, enumeration, enumType)
		addProperty(property)
		return property
	}

	protected fun <E : Enumerated> addEnumSetProperty(
		name: String,
		enumType: Class<E>,
	): EnumSetProperty<E> {
		val enumeration: Enumeration<E> = directory.getEnumeration(enumType)
		val property: EnumSetProperty<E> = EnumSetPropertyImpl(this, name, enumeration)
		addProperty(property)
		return property
	}

	fun <A : Aggregate> addReferenceProperty(
		name: String,
		aggregateType: Class<A>,
	): AggregateReferenceProperty<A> {
		val repo: AggregateRepository<A> = directory.getRepository(aggregateType)
		val property: AggregateReferenceProperty<A> =
			AggregateReferencePropertyImpl(this, name, repo, aggregateType)
		addProperty(property)
		return property
	}

	protected fun <A : Aggregate> addReferenceSetProperty(
		name: String,
		aggregateType: Class<A>,
	): ReferenceSetProperty<A> {
		val repo: AggregateRepository<A> = directory.getRepository(aggregateType)
		val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(this, name, repo)
		addProperty(property)
		return property
	}

	fun <P : Part<*>> addPartListProperty(
		name: String,
		partType: Class<P>,
	): PartListProperty<P> {
		val property: PartListProperty<P> = PartListPropertyImpl(this, name, partType)
		addProperty(property)
		return property
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <A : Aggregate, P : Part<A>> addPartReferenceProperty(
		name: String,
		partType: Class<P>,
	): PartReferenceProperty<P> {
		val property: PartReferenceProperty<P> = PartReferencePropertyImpl(
			this,
			name,
			{ id: Int -> getPart(id) as P },
			partType,
		)
		addProperty(property)
		return property
	}

	// ============================================================================
	// getOrAdd* methods for property delegates
	// These methods return existing properties or create new ones on first access.
	// ============================================================================

	/**
	 * Gets an existing base property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T : Any> getOrAddBaseProperty(
		name: String,
		type: Class<T>,
	): BaseProperty<T> {
		if (hasProperty(name)) {
			return propertyMap[name] as BaseProperty<T>
		}
		return addBaseProperty(name, type)
	}

	/**
	 * Gets an existing enum property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <E : Enumerated> getOrAddEnumProperty(
		name: String,
		enumType: Class<E>,
	): EnumProperty<E> {
		if (hasProperty(name)) {
			return propertyMap[name] as EnumProperty<E>
		}
		val enumeration: Enumeration<E> = directory.getEnumeration(enumType)
		val property: EnumProperty<E> = EnumPropertyImpl(this, name, enumeration, enumType)
		addProperty(property)
		return property
	}

	/**
	 * Gets an existing enum set property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <E : Enumerated> getOrAddEnumSetProperty(
		name: String,
		enumType: Class<E>,
	): EnumSetProperty<E> {
		if (hasProperty(name)) {
			return propertyMap[name] as EnumSetProperty<E>
		}
		val enumeration: Enumeration<E> = directory.getEnumeration(enumType)
		val property: EnumSetProperty<E> = EnumSetPropertyImpl(this, name, enumeration)
		addProperty(property)
		return property
	}

	/**
	 * Gets an existing aggregate reference property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <A : Aggregate> getOrAddReferenceProperty(
		name: String,
		aggregateType: Class<A>,
	): AggregateReferenceProperty<A> {
		if (hasProperty(name)) {
			return propertyMap[name] as AggregateReferenceProperty<A>
		}
		val repo: AggregateRepository<A> = directory.getRepository(aggregateType)
		val property: AggregateReferenceProperty<A> =
			AggregateReferencePropertyImpl(this, name, repo, aggregateType)
		addProperty(property)
		return property
	}

	/**
	 * Gets an existing reference set property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <A : Aggregate> getOrAddReferenceSetProperty(
		name: String,
		aggregateType: Class<A>,
	): ReferenceSetProperty<A> {
		if (hasProperty(name)) {
			return propertyMap[name] as ReferenceSetProperty<A>
		}
		val repo: AggregateRepository<A> = directory.getRepository(aggregateType)
		val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(this, name, repo)
		addProperty(property)
		return property
	}

	/**
	 * Gets an existing part list property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <P : Part<*>> getOrAddPartListProperty(
		name: String,
		partType: Class<P>,
	): PartListProperty<P> {
		if (hasProperty(name)) {
			return propertyMap[name] as PartListProperty<P>
		}
		val property: PartListProperty<P> = PartListPropertyImpl(this, name, partType)
		addProperty(property)
		return property
	}

	/**
	 * Gets an existing part reference property or creates a new one if it doesn't exist.
	 * Used by property delegates for lazy property registration.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <P : Part<*>> getOrAddPartReferenceProperty(
		name: String,
		partType: Class<P>,
	): PartReferenceProperty<P> {
		if (hasProperty(name)) {
			return propertyMap[name] as PartReferenceProperty<P>
		}
		val property: PartReferenceProperty<P> = PartReferencePropertyImpl(
			this,
			name,
			{ id: Int -> getPart(id) as P },
			partType,
		)
		addProperty(property)
		return property
	}

	override val parentProperty: Property<*>?
		get() = null

	override fun fireFieldChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	) {
	}

	override fun fireEntityAddedChange(id: Any) {
		if (!isInLoad && doLogChange(this)) {
			var path = path
			val partEndIndex = path.lastIndexOf(".")
			val aggregateEndIndex = path.lastIndexOf("(")
			path = path.substring(0, Integer.max(partEndIndex, aggregateEndIndex))
			fireFieldChange("add", path, id.toString(), null, isInCalc())
		}
	}

	override fun fireEntityRemovedChange() {
		if (!isInLoad && doLogChange(this)) {
			fireFieldChange("remove", path, null, null, isInCalc())
		}
	}

	override fun fireValueAddedChange(
		property: Property<*>,
		value: Any,
	) {
		if (!isInLoad && doLogChange(this)) {
			fireFieldChange("add", property.path, value.toString(), null, isInCalc())
		}
	}

	override fun fireValueRemovedChange(
		property: Property<*>,
		value: Any,
	) {
		if (!isInLoad && doLogChange(this)) {
			fireFieldChange("remove", property.path, value.toString(), null, isInCalc())
		}
	}

	override fun fireFieldSetChange(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
		if (!isInLoad && doLogChange(property)) {
			val op = if (oldValue == null) "add" else "replace"
			fireFieldChange(op, property.path, value?.toString(), oldValue.toString(), isInCalc())
		}
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> = throw UnsupportedOperationException()

	override fun doBeforeSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
	}

	override fun doAfterSet(property: Property<*>) {}

	override fun doAfterClear(property: Property<*>) {}

	override fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	) {
	}

	override fun doAfterRemove(property: Property<*>) {}

	protected fun doLogChange(entity: EntityWithProperties): Boolean {
		val parentProperty = (entity as EntityWithPropertiesBase).parentProperty
		return parentProperty == null || doLogChange(parentProperty)
	}

	protected fun doLogChange(property: Property<*>): Boolean {
		val entity = (property.entity as EntityWithPropertiesBase)
		if (!doLogChange(entity)) {
			return false
		}
		return entity.doLogChange(property.name)
	}

	protected abstract fun doLogChange(propertyName: String): Boolean

}
