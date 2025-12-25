package io.dddrive.ddd.model.base

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.AggregateSPI
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.RepositoryDirectory
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.Property
import io.dddrive.property.model.PropertyChangeListener
import io.dddrive.property.model.base.EntityWithPropertiesBase
import io.dddrive.validation.model.AggregatePartValidation
import io.dddrive.validation.model.enums.CodeValidationLevel
import io.dddrive.validation.model.impl.AggregatePartValidationImpl
import java.time.OffsetDateTime
import java.util.function.Consumer

/** A DDD Aggregate */
abstract class AggregateBase(
	override val repository: AggregateRepository<out Aggregate>,
	override val isNew: Boolean,
) : EntityWithPropertiesBase(),
	Aggregate,
	AggregateMeta,
	AggregateSPI {

	// ============================================================================
	// Delegated properties (Aggregate interface)
	// ============================================================================

	private var _id: Any? by baseProperty()
	override val id: Any get() = _id!!

	private var _maxPartId: Int? by baseProperty()

	protected var _version: Int? by baseProperty()
	override val version: Int get() = _version ?: 0

	// tenant reference - use explicit property access
	// Both tenantId and tenant access the same "tenant" reference property
	private val tenantProperty by lazy { getOrAddReferenceProperty("tenant", ObjTenant::class.java) }
	override val tenantId: Any get() = tenantProperty.id!!
	override val tenant: ObjTenant get() = tenantProperty.value!!

	// owner reference - use explicit property access
	// Both ownerId and owner access the same "owner" reference property
	private val ownerProperty by lazy { getOrAddReferenceProperty("owner", ObjUser::class.java) }
	var ownerId: Any?
		get() = ownerProperty.id
		set(value) {
			ownerProperty.id = value
		}
	override var owner: ObjUser?
		get() = ownerProperty.value
		set(value) {
			ownerProperty.value = value
		}

	protected var _caption: String? by baseProperty()
	override val caption: String get() = _caption ?: ""

	// ============================================================================
	// Delegated properties (AggregateMeta interface)
	// ============================================================================

	// createdByUser reference - use explicit property access
	private val createdByUserProperty by lazy { getOrAddReferenceProperty("createdByUser", ObjUser::class.java) }
	var createdByUserId: Any?
		get() = createdByUserProperty.id
		set(value) {
			createdByUserProperty.id = value
		}
	override val createdByUser: ObjUser get() = createdByUserProperty.value!!

	private val createdAtProperty by lazy { getOrAddBaseProperty("createdAt", OffsetDateTime::class.java) }
	var _createdAt: OffsetDateTime?
		get() = createdAtProperty.value
		set(value) {
			createdAtProperty.value = value
		}
	override val createdAt: OffsetDateTime get() = createdAtProperty.value!!

	// modifiedByUser reference - use explicit property access
	override var modifiedByUser: ObjUser? by referenceProperty()
	protected var modifiedByUserId: Any? by referenceIdProperty<ObjUser>()
	// private val modifiedByUserProperty by lazy { getOrAddReferenceProperty("modifiedByUser", ObjUser::class.java) }
	// var modifiedByUserId: Any?
	// 	get() = modifiedByUserProperty.id
	// 	set(value) {
	// 		modifiedByUserProperty.id = value
	// 	}
	// override var modifiedByUser: ObjUser?
	// 	get() = modifiedByUserProperty.value
	// 	set(value) {
	// 		modifiedByUserProperty.value = value
	// 	}

	override var modifiedAt: OffsetDateTime? by baseProperty()

	// ============================================================================
	// Internal state
	// ============================================================================

	private val _propertyChangeListeners: MutableSet<PropertyChangeListener> = mutableSetOf()
	private val _validations: MutableList<AggregatePartValidation> = mutableListOf()

	private var _isFrozen = false
	private var _isInLoad = false
	private var isCalcDisabled = 0
	private var _isInCalc = false

	var doInitSeqNr: Int = 0
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

	// Trigger property initialization before persistence layer access through setValueByPath.
	// Delegated properties register on first access.
	// Lazy properties are accessed to trigger initialization.
	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		_id
		_maxPartId
		_version
		tenantProperty
		ownerProperty
		_caption
		createdByUserProperty
		_createdAt
		modifiedByUser
		modifiedByUserId
		modifiedAt
		doInitSeqNr += 1
	}

	final override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	) {
		_id = aggregateId
		tenantProperty.id = tenantId
		doCreateSeqNr += 1
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		ownerId = userId
		_version = 0
		createdByUserId = userId
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
