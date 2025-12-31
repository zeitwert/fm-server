package dddrive.ddd.property.model

interface PropertyChangeListener {

	fun propertyChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
	)

}
