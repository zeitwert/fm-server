package io.dddrive.core.property.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration
import io.dddrive.core.property.model.AggregateReferenceProperty
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.EnumSetProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceSetProperty
import io.dddrive.core.property.model.impl.AggregateReferencePropertyImpl
import io.dddrive.core.property.model.impl.BasePropertyImpl
import io.dddrive.core.property.model.impl.EnumPropertyImpl
import io.dddrive.core.property.model.impl.EnumSetPropertyImpl
import io.dddrive.core.property.model.impl.PartListPropertyImpl
import io.dddrive.core.property.model.impl.PartReferencePropertyImpl
import io.dddrive.core.property.model.impl.ReferenceSetPropertyImpl
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class EntityWithPropertiesBase :
	EntityWithProperties,
	EntityWithPropertiesSPI {

	private val propertyMap: MutableMap<String, Property<*>> = mutableMapOf()
	private val partMap: MutableMap<Int, Part<*>> = mutableMapOf()

	override fun hasProperty(name: String): Boolean = propertyMap.containsKey(name)

	override fun getProperty(name: String): Property<*> = propertyMap[name]!!

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

	fun <T> addBaseProperty(
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
			AggregateReferencePropertyImpl(this, name, { id: Any -> repo.get(id)!! }, aggregateType)
		addProperty(property)
		return property
	}

	protected fun <A : Aggregate> addReferenceSetProperty(
		name: String,
		aggregateType: Class<A>,
	): ReferenceSetProperty<A> {
		val repo: AggregateRepository<A> = directory.getRepository(aggregateType)
		val property: ReferenceSetProperty<A> = ReferenceSetPropertyImpl(this, name, { id: Any -> repo.get(id)!! })
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

	override fun <T> setValueByPath(
		relativePath: String,
		value: T?,
	) {
		check(!isFrozen) { "Cannot set value on a frozen entity." }
		require(relativePath.isNotEmpty()) { "path cannot be empty" }
		val property = getPropertyByPath<T>(relativePath)
		require(property is BaseProperty<*>) { "property '" + property.name + "' is not a simple property and cannot be set directly. Path: " + relativePath }
		(property as BaseProperty<T>).value = value
	}

	override fun <T> getPropertyByPath(relativePath: String): Property<T> {
		require(relativePath.isNotEmpty()) { "path cannot be empty" }
		val pathSegments = relativePath.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		require(pathSegments.isNotEmpty()) { "path is empty after splitting: $relativePath" }
		return findPropertyRecursive(this, pathSegments, 0, relativePath)
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> findPropertyRecursive(
		currentEntity: EntityWithProperties,
		pathSegments: Array<String>,
		currentIndex: Int,
		originalPath: String,
	): Property<T> {
		val segment = pathSegments[currentIndex]
		val isLastSegment = (currentIndex == pathSegments.size - 1)

		val matcher: Matcher = SEGMENT_PATTERN.matcher(segment)

		require(matcher.matches()) { "Invalid path segment format: '" + segment + "' in path '" + originalPath + "'" }

		val propName = matcher.group(1)
		val indexStr = matcher.group(2)

		val property = currentEntity.getProperty(propName)
		requireNotNull(property) { "Property '" + propName + "' not found on entity " + currentEntity.javaClass.getSimpleName() + " in path '" + originalPath + "'" }

		if (indexStr != null) {
			require(property is PartListProperty<*>) { "Property '" + propName + "' is not a list, but path specifies an index. Path: " + originalPath }
			val index = indexStr.toInt()

			if (index < 0 || index >= property.partCount) {
				throw IndexOutOfBoundsException("Index " + index + " out of bounds for list '" + propName + "' (size: " + property.partCount + ") in path '" + originalPath + "'")
			}

			val nextEntity: EntityWithProperties? = property.getPart(index)
			checkNotNull(nextEntity) { "Part at index " + index + " in list '" + propName + "' is null. Path: " + originalPath }

			require(!isLastSegment) { "Path cannot end with a direct list access. Path: " + originalPath }

			return findPropertyRecursive(nextEntity, pathSegments, currentIndex + 1, originalPath)
		}

		if (isLastSegment) {
			return property as Property<T>
		}

		val nextSegment: String? = pathSegments[currentIndex + 1]

		if (currentIndex + 2 == pathSegments.size && "id" == nextSegment) {
			if (property is AggregateReferenceProperty<*>) {
				return property.idProperty as Property<T>
			} else if (property is PartReferenceProperty<*>) {
				return property.idProperty as Property<T>
			}
		}

		require(!((property is AggregateReferenceProperty<*> || property is PartReferenceProperty<*>) && "id" == nextSegment)) { "cannot navigate deeper from '.id' of a reference property. Path: $originalPath" }

		val nextEntity: EntityWithProperties = getEntityWithProperties(originalPath, property, propName)
		return findPropertyRecursive(nextEntity, pathSegments, currentIndex + 1, originalPath)
	}

	companion object {

		private val SEGMENT_PATTERN: Pattern = Pattern.compile("(\\w+)(?:\\[(\\d+)])?")

		private fun getEntityWithProperties(
			originalPath: String?,
			property: Property<*>?,
			propName: String?,
		): EntityWithProperties {
			val nextEntity: EntityWithProperties
			nextEntity = if (property is PartReferenceProperty<*>) {
				property.value!!
			} else if (property is AggregateReferenceProperty<*>) {
				property.value!!
			} else {
				throw IllegalArgumentException("Property '" + propName + "' is not a container (PartList, PartReference, or Reference). Cannot navigate deeper in path '" + originalPath + "'")
			}

			checkNotNull(nextEntity) { "Reference '" + propName + "' is null. Path: " + originalPath }
			return nextEntity
		}
	}

}
