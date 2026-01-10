package dddrive.ddd.property.model

import dddrive.ddd.model.Part
import dddrive.ddd.model.RepositoryDirectory

interface EntityWithPropertiesSPI {

	val directory: RepositoryDirectory

	fun hasPart(partId: Int): Boolean

	fun getPart(partId: Int): Part<*>

	fun doLogChange(): Boolean

	fun doLogChange(property: Property<*>): Boolean

	fun fireFieldChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
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

	fun doAfterSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	)

	fun doAfterClear(property: Property<*>)

	fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	)

	fun doAfterRemove(property: Property<*>)

}
