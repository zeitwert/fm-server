package dddrive.ddd.core.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateMeta
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.PropertyChangeListener
import dddrive.ddd.validation.model.AggregatePartValidation
import dddrive.ddd.validation.model.enums.CodeValidationLevel
import dddrive.ddd.validation.model.impl.AggregatePartValidationImpl
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
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
	protected var _version: Int? by baseProperty(this, "version")
	protected var _tenantId: Any? by referenceIdProperty<ObjTenant>(this, "tenant")
	protected var _tenant: ObjTenant? by referenceProperty(this, "tenant")
	protected var _createdByUserId: Any? by referenceIdProperty<ObjUser>(this, "createdByUser")
	protected var _createdByUser: ObjUser? by referenceProperty(this, "createdByUser")
	protected var _createdAt: OffsetDateTime? by baseProperty(this, "createdAt")
	protected var _caption: String? by baseProperty(this, "caption")

	override val id: Any get() = _id!!
	override val version: Int get() = _version!!
	override val tenantId: Any get() = _tenantId!!
	override val tenant: ObjTenant get() = _tenant!!
	override val createdByUser: ObjUser get() = _createdByUser!!
	override val createdAt: OffsetDateTime get() = _createdAt!!
	override val caption: String get() = _caption ?: ""

	protected var ownerId: Any? by referenceIdProperty<ObjUser>(this, "owner")
	override var owner: ObjUser? by referenceProperty(this, "owner")

	protected var modifiedByUserId: Any? by referenceIdProperty<ObjUser>(this, "modifiedByUser")
	override var modifiedByUser: ObjUser? by referenceProperty(this, "modifiedByUser")
	override var modifiedAt: OffsetDateTime? by baseProperty(this, "modifiedAt")

	private var _maxPartId: Int? by baseProperty(this, "maxPartId")
	private val _propertyChangeListeners: MutableSet<PropertyChangeListener> = mutableSetOf()
	private val _validations: MutableList<AggregatePartValidation> = mutableListOf()

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

	final override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	) {
		_id = aggregateId
		_tenantId = tenantId
		doCreateSeqNr += 1
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		ownerId = userId
		_version = 0
		_createdByUserId = userId
		_createdAt = timestamp
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

	private fun clearValidationList() {
		_validations.clear()
	}

	override val validations: List<AggregatePartValidation>
		get() = _validations.toList()

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		entity: EntityWithPropertiesSPI,
	) {
		addValidation(validationLevel, validation, entity.relativePath)
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		property: Property<*>,
	) {
		addValidation(validationLevel, validation, property.relativePath)
	}

	fun addValidation(
		validationLevel: CodeValidationLevel,
		validation: String,
		path: String? = null,
	) {
		_validations.add(
			AggregatePartValidationImpl(validations.size, validationLevel, validation, path),
		)
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

	protected fun beginCalc() {
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
			clearValidationList()
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

	override fun toString(): String = caption

}
