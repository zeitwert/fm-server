package dddrive.ddd.property.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.path.path
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.Property
import kotlin.reflect.KClass

abstract class EntityWithPropertiesBase :
	EntityWithProperties,
	EntityWithPropertiesSPI {

	private val propertyMap: MutableMap<String, Property<*>> = mutableMapOf()
	private val partMap: MutableMap<Int, Part<*>> = mutableMapOf()

	override fun hasProperty(name: String): Boolean = propertyMap.containsKey(name) || propertyMap.containsKey("_$name")

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

	override fun fireFieldChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	) {
	}

	override fun fireEntityAddedChange(id: Any) {
		if (!isInLoad && doLogChange()) {
			var path = path()
			val partEndIndex = path.lastIndexOf(".")
			val aggregateEndIndex = path.lastIndexOf("(")
			path = path.substring(0, Integer.max(partEndIndex, aggregateEndIndex))
			fireFieldChange("add", path, id.toString(), null, isInCalc)
		}
	}

	override fun fireEntityRemovedChange() {
		if (!isInLoad && doLogChange()) {
			fireFieldChange("remove", path(), null, null, isInCalc)
		}
	}

	override fun fireValueAddedChange(
		property: Property<*>,
		value: Any,
	) {
		if (!isInLoad && doLogChange()) {
			fireFieldChange("add", property.path(), value.toString(), null, isInCalc)
		}
	}

	override fun fireValueRemovedChange(
		property: Property<*>,
		value: Any,
	) {
		if (!isInLoad && doLogChange()) {
			fireFieldChange("remove", property.path(), value.toString(), null, isInCalc)
		}
	}

	override fun fireFieldSetChange(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
		if (!isInLoad && doLogChange(property)) {
			val op = if (oldValue == null) "add" else "replace"
			fireFieldChange(op, property.path(), value?.toString(), oldValue.toString(), isInCalc)
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

	protected fun doLogChange(): Boolean =
		if (this is Aggregate) {
			true
		} else {
			doLogChange((this as Part<*>).meta.parentProperty)
		}

	protected fun doLogChange(property: Property<*>): Boolean {
		val entity = (property.entity as EntityWithPropertiesBase)
		if (!entity.doLogChange()) {
			return false
		}
		return entity.doLogChange(property.name)
	}

	protected abstract fun doLogChange(propertyName: String): Boolean

}
