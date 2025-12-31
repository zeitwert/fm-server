package dddrive.ddd.core.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateMeta
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.PropertyChangeListener
import dddrive.ddd.property.model.base.EntityWithPropertiesBase
import java.util.function.Consumer

/**
 * A DDD Aggregate
 */
abstract class AggregateBase(
	override val repository: AggregateRepository<out Aggregate>,
	override val isNew: Boolean,
) : EntityWithPropertiesBase(),
	Aggregate,
	AggregateMeta,
	AggregateSPI {

	protected var _id by baseProperty<Any>("id")
	override val id get() = _id!!

	protected var _version by baseProperty<Int>("version")
	override val version get() = _version!!

	var maxPartId by baseProperty<Int>("maxPartId")

	override var isFrozen = false
	override var isInLoad = false
	override var isInCalc = false

	private var isCalcDisabled = 0

	var doAfterCreateSeqNr: Int = 0
	var doAfterLoadSeqNr: Int = 0
	var doBeforeStoreSeqNr: Int = 0
	var doAfterStoreSeqNr: Int = 0
	private var didCalcAll = false
	private var didCalcVolatile = false

	private val _propertyChangeListeners: MutableSet<PropertyChangeListener> = mutableSetOf()

	override val directory: RepositoryDirectory get() = repository.directory

	override val meta: AggregateMeta
		get() = this

	override fun addPropertyChangeListener(listener: PropertyChangeListener) {
		_propertyChangeListeners.add(listener)
	}

	override fun removePropertyChangeListener(listener: PropertyChangeListener) {
		_propertyChangeListeners.remove(listener)
	}

	override fun fireFieldChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
	) {
		_propertyChangeListeners.forEach(
			Consumer { listener: PropertyChangeListener ->
				listener.propertyChange(op, path, value, oldValue, isInCalc)
			},
		)
	}

	override fun doLogChange(propertyName: String): Boolean = repository.doLogChange(propertyName)

	override fun <P : Part<*>> nextPartId(partClass: Class<P>): Int {
		synchronized(this) {
			maxPartId = (maxPartId ?: 0) + 1
			return maxPartId!!
		}
	}

	override fun doAfterCreate() {
		doAfterCreateSeqNr += 1
		_version = 0
	}

	override fun doAfterLoad() {
		doAfterLoadSeqNr += 1
	}

	override fun doBeforeStore() {
		doBeforeStoreSeqNr += 1
	}

	override fun doAfterStore() {
		doAfterStoreSeqNr += 1
	}

	protected fun unfreeze() {
		isFrozen = false
	}

	fun freeze() {
		isFrozen = true
	}

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> =
		throw RuntimeException(
			"did not instantiate part for property " + className + "." + property.name,
		)

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
		calcAll()
	}

	override fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	) {
		if (part != null) {
			addPart(part)
		}
		calcAll()
	}

	override fun doAfterRemove(property: Property<*>) {
		calcAll()
	}

	override fun doAfterClear(property: Property<*>) {
		calcAll()
	}

	override fun beginLoad() {
		isInLoad = true
	}

	override fun endLoad() {
		isInLoad = false
	}

	override val isCalcEnabled get() = isCalcDisabled == 0

	override fun disableCalc() {
		isCalcDisabled += 1
	}

	override fun enableCalc() {
		isCalcDisabled -= 1
	}

	protected open fun beginCalc() {
		isInCalc = true
		didCalcAll = false
		didCalcVolatile = false
	}

	protected fun endCalc() {
		isInCalc = false
	}

	override fun calcAll() {
		if (!isCalcEnabled || isInCalc) {
			return
		}
		try {
			beginCalc()
			doCalcAll()
			check(didCalcAll) { "$className: doCalcAll was propagated" }
		} finally {
			endCalc()
		}
	}

	protected open fun doCalcAll() {
		didCalcAll = true
	}

	override fun calcVolatile() {
		if (!isCalcEnabled || isInCalc) {
			return
		}
		try {
			beginCalc()
			doCalcVolatile()
			check(didCalcVolatile) { "$className: doCalcVolatile was propagated" }
		} finally {
			endCalc()
		}
	}

	protected open fun doCalcVolatile() {
		didCalcVolatile = true
	}

	private val className: String
		get() = javaClass.getSuperclass().getSimpleName()

}
