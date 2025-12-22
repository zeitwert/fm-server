package io.dddrive.property.model

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.RepositoryDirectory

interface EntityWithPropertiesSPI {

	val directory: RepositoryDirectory

	fun doInit()

	val isInLoad: Boolean

	fun isInCalc(): Boolean

	val parentProperty: Property<*>?

	val relativePath: String

	val path: String

	fun fireFieldChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	)

	fun fireEntityAddedChange(id: Any)

	fun fireEntityRemovedChange()

	fun fireValueAddedChange(
		property: Property<*>,
		value: Any,
	)

	fun fireValueRemovedChange(
		property: Property<*>,
		value: Any,
	)

	fun fireFieldSetChange(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	)

	fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*>

	fun doBeforeSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	)

	fun doAfterSet(property: Property<*>)

	fun doAfterClear(property: Property<*>)

	fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	)

	fun doAfterRemove(property: Property<*>)

}
