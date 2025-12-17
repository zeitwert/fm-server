package io.dddrive.core.property.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.util.Invariant

abstract class ReferencePropertyBase<E : Any, ID : Any>(
	entity: EntityWithProperties,
	name: String,
	private val idType: Class<ID>,
) : PropertyBase<E>(entity, name) {

	val idProperty: BaseProperty<ID>
	open var id: ID? = null
		set(id) {
			Invariant.requireThis(this.isWritable, "not frozen")
			if (field == id) {
				return
			}
			Invariant.assertThis(isValidId(id), "valid id [$id]")
			val entity = this.entity as EntityWithPropertiesSPI
			entity.doBeforeSet(this, id, field)
			entity.fireFieldSetChange(this, id, field)
			field = id
			entity.doAfterSet(this)
		}

	init {
		this.idProperty = IdProperty()
	}

	protected abstract fun isValidId(id: ID?): Boolean

	private inner class IdProperty : BaseProperty<ID> {

		override val entity: EntityWithProperties
			get() = this@ReferencePropertyBase.entity

		override val relativePath: String
			get() {
				val relativePath = this@ReferencePropertyBase.relativePath
				return if (relativePath.isEmpty()) ID_PROPERTY_NAME else "$relativePath.$ID_PROPERTY_NAME"
			}

		override val path: String
			get() = this@ReferencePropertyBase.path + "." + ID_PROPERTY_NAME

		override val name: String
			get() = ID_PROPERTY_NAME

		override val isWritable: Boolean
			get() = this@ReferencePropertyBase.isWritable

		override var value: ID?
			get() = this@ReferencePropertyBase.id
			set(value) {
				this@ReferencePropertyBase.id = value!!
			}

		override val type: Class<ID>
			get() = idType
	}

	companion object {

		private const val ID_PROPERTY_NAME = "id"
	}

}
