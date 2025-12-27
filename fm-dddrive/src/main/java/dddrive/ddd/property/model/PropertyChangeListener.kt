package dddrive.ddd.property.model

interface PropertyChangeListener {

	fun propertyChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	)

}
