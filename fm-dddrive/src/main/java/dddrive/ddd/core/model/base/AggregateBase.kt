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
import java.time.OffsetDateTime
import java.util.function.Consumer

/**
 * A DDD Aggregate
 */
abstract class AggregateBase(
	override val repository: AggregateRepository<out Aggregate>,
	override val isNew: Boolean,
) : dddrive.ddd.property.model.base.EntityWithPropertiesBase(),
	Aggregate,
	AggregateMeta,
	AggregateSPI {

	protected var _id: Any? by baseProperty(this, "id")
	override val id: Any get() = _id!!

	protected var _version: Int? by baseProperty(this, "version")
	override val version: Int get() = _version!!

	private var _maxPartId: Int? by baseProperty(this, "maxPartId")
	private val _propertyChangeListeners: MutableSet<PropertyChangeListener> = mutableSetOf()

	private var _isFrozen = false
	private var _isInLoad = false
	private var isCalcDisabled = 0
	private var _isInCalc = false

	var doCreateSeqNr: Int = 0
	var doAfterCreateSeqNr: Int = 0
	var doAfterLoadSeqNr: Int = 0
	var doBeforeStoreSeqNr: Int = 0
	var doAfterStoreSeqNr: Int = 0
	private var didCalcAll = false
	private var didCalcVolatile = false

	override val directory: RepositoryDirectory get() = repository.directory

	override val meta: AggregateMeta
		get() = this

	override val relativePath: String
		get() = ""

	override val path: String
		get() = repository.aggregateType.id + "(" + id + ")"

	override fun addPropertyChangeListener(listener: PropertyChangeListener) {
		_propertyChangeListeners.add(listener)
	}

	override fun removePropertyChangeListener(listener: PropertyChangeListener) {
		_propertyChangeListeners.remove(listener)
	}

	override fun fireFieldChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
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
			_maxPartId = (_maxPartId ?: 0) + 1
			return _maxPartId!!
		}
	}

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	) {
		_id = aggregateId
		doCreateSeqNr += 1
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		_version = 0
		doAfterCreateSeqNr += 1
		fireEntityAddedChange(id)
	}

	override fun doAfterLoad() {
		doAfterLoadSeqNr += 1
	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		doBeforeStoreSeqNr += 1
	}

	override fun doAfterStore() {
		doAfterStoreSeqNr += 1
	}

	override val isFrozen: Boolean
		get() = _isFrozen

	protected fun unfreeze() {
		_isFrozen = false
	}

	fun freeze() {
		_isFrozen = true
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

	override fun doAfterSet(property: Property<*>) {
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

	override val isInLoad: Boolean
		get() = _isInLoad

	override fun beginLoad() {
		_isInLoad = true
	}

	override fun endLoad() {
		_isInLoad = false
	}

	override fun isCalcEnabled(): Boolean = isCalcDisabled == 0

	override fun disableCalc() {
		isCalcDisabled += 1
	}

	override fun enableCalc() {
		isCalcDisabled -= 1
	}

	override fun isInCalc(): Boolean = _isInCalc

	protected open fun beginCalc() {
		_isInCalc = true
		didCalcAll = false
		didCalcVolatile = false
	}

	protected fun endCalc() {
		_isInCalc = false
	}

	override fun calcAll() {
		if (!isCalcEnabled() || isInCalc()) {
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
		if (!isCalcEnabled() || isInCalc()) {
			return
		}
		try {
			beginCalc()
			doCalcVolatile()
			check(didCalcVolatile) { "$className: doCalcAll was propagated" }
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
