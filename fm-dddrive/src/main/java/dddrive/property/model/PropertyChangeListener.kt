package dddrive.property.model

interface PropertyChangeListener {

	fun propertyChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
	)

}
