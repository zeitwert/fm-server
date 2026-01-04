package dddrive.ddd.property.model.base

import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.ReferenceProperty

abstract class ReferencePropertyBase<T : Any, ID : Any>(
	entity: EntityWithProperties,
	name: String,
	idType: Class<ID>,
) : PropertyBase<T>(entity, name),
	ReferenceProperty<T, ID> {

	override val idProperty = IdProperty<T, ID>(this, idType)

	override var id: ID? = null
		set(id) {
			require(this.isWritable) { "not frozen" }
			require(isValidId(id)) { "valid id [$id]" }
			if (field == id) {
				return
			}
			val entity = this.entity as EntityWithPropertiesSPI
			val oldId = field
			entity.doBeforeSet(this, id, oldId)
			field = id
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
