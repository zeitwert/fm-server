package dddrive.property.model.base

import dddrive.property.model.BaseProperty
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EntityWithPropertiesSPI
import dddrive.property.model.ReferenceProperty

abstract class ReferencePropertyBase<T : Any, ID : Any>(
	entity: EntityWithProperties,
	name: String,
	idType: Class<ID>,
	private val idCalculator: ((ReferenceProperty<T, ID>) -> ID?)? = null,
) : PropertyBase<T>(entity, name, idCalculator != null),
	ReferenceProperty<T, ID> {

	override val idProperty = IdProperty<T, ID>(this, idType)

	private var storedId: ID? = null

	override var id: ID?
		get() = idCalculator?.invoke(this) ?: storedId
		set(id) {
			require(this.isWritable) { "not frozen" }
			require(isValidId(id)) { "valid id [$id]" }
			if (storedId == id) {
				return
			}
			val entity = this.entity as EntityWithPropertiesSPI
			val oldId = storedId
			entity.doBeforeSet(this, id, oldId)
			storedId = id
			fireFieldSetChange(id, oldId)
			entity.doAfterSet(this, id, oldId)
		}

	protected abstract fun isValidId(id: ID?): Boolean

	override fun toString(): String = "$name: $id"

}

class IdProperty<T : Any, ID : Any>(
	val baseProperty: ReferenceProperty<T, ID>,
	val idType: Class<ID>,
) : BaseProperty<ID> {

	override val entity: EntityWithProperties
		get() = baseProperty.entity

	override val name: String
		get() = ID_PROPERTY_NAME

	override val isWritable: Boolean
		get() = baseProperty.isWritable

	override var value: ID?
		get() = baseProperty.id
		set(value) {
			baseProperty.id = value
		}

	override val type: Class<ID>
		get() = idType

	companion object {

		private const val ID_PROPERTY_NAME = "id"
	}

}
