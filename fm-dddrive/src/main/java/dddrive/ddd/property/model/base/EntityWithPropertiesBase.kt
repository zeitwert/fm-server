package dddrive.ddd.property.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.Property
import kotlin.reflect.KClass

abstract class EntityWithPropertiesBase :
	EntityWithProperties,
	EntityWithPropertiesSPI {

	private val propertyMap: MutableMap<String, Property<*>> = mutableMapOf()
	private val partMap: MutableMap<Int, Part<*>> = mutableMapOf()

	override fun hasProperty(name: String): Boolean = propertyMap.containsKey(name)

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T> {
		require(propertyMap.containsKey(name)) { "property [$name] not found in ${javaClass.simpleName}" }
		return propertyMap[name]!! as Property<T>
	}

	override val properties: List<Property<*>>
		get() = propertyMap.values.toList()

	override fun hasPart(partId: Int): Boolean = partMap.containsKey(partId)

	override fun getPart(partId: Int): Part<*> = partMap[partId]!!

	protected fun addPart(part: Part<*>) {
		partMap[part.id] = part
	}

	override fun addProperty(property: Property<*>) {
		require(!hasProperty(property.name)) { "property [" + property.name + "] is unique" }
		propertyMap.put(property.name, property)
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

	override fun doAfterSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
	}

	override fun doAfterClear(property: Property<*>) {}

	override fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	) {
	}

	override fun doAfterRemove(property: Property<*>) {}

	override fun doLogChange(): Boolean =
		if (this is Aggregate) {
			true
		} else {
			doLogChange((this as Part<*>).meta.parentProperty)
		}

	override fun doLogChange(property: Property<*>): Boolean {
		val entity = (property.entity as EntityWithPropertiesBase)
		if (!entity.doLogChange()) {
			return false
		}
		return entity.doLogChange(property.name)
	}

	protected abstract fun doLogChange(propertyName: String): Boolean

}
