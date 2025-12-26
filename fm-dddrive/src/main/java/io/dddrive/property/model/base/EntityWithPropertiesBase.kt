package io.dddrive.property.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.Property
import kotlin.reflect.KClass

abstract class EntityWithPropertiesBase :
	EntityWithProperties,
	EntityWithPropertiesSPI {

	private val propertyMap: MutableMap<String, Property<*>> = mutableMapOf()
	private val partMap: MutableMap<Int, Part<*>> = mutableMapOf()

	override fun hasProperty(name: String): Boolean = propertyMap.containsKey(name) || propertyMap.containsKey("_$name")

	/**
	 * Resolves the actual property name, trying underscore-prefix fallback.
	 * Returns the name that exists in the map, or the original name if neither exists.
	 */
	private fun resolvePropertyName(name: String): String =
		when {
			propertyMap.containsKey(name) -> name
			propertyMap.containsKey("_$name") -> "_$name"
			else -> name // Let it fail with the original name
		}

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> {
		val resolvedName = resolvePropertyName(name)
		require(propertyMap.containsKey(resolvedName)) { "property [$name] not found in ${javaClass.simpleName}" }
		return propertyMap[resolvedName]!! as Property<T>
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
