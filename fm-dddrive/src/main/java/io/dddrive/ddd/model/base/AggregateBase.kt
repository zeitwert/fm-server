package io.dddrive.ddd.model.base

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.AggregateSPI
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.RepositoryDirectory
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.BaseProperty
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
		val maxPartId = getProperty("maxPartId", Int::class) as BaseProperty<Int>
		synchronized(maxPartId) {
			maxPartId.value = (maxPartId.value ?: 0) + 1
			return maxPartId.value!!
		}
	}

	override fun doInit() {
		addBaseProperty("id", Any::class.java)
		addBaseProperty("maxPartId", Int::class.java)
		addBaseProperty("version", Int::class.java)
		addReferenceProperty("tenant", ObjTenant::class.java)
		addReferenceProperty("owner", ObjUser::class.java)
		addBaseProperty("caption", String::class.java)
		addReferenceProperty("createdByUser", ObjUser::class.java)
		addBaseProperty("createdAt", OffsetDateTime::class.java)
		addReferenceProperty("modifiedByUser", ObjUser::class.java)
		addBaseProperty("modifiedAt", OffsetDateTime::class.java)
		doInitSeqNr += 1
	}

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		fireEntityAddedChange(aggregateId)
		setValueByPath("id", aggregateId)
		setValueByPath("tenantId", tenantId)
		setValueByPath("createdByUserId", userId)
		setValueByPath("createdAt", timestamp)
		doCreateSeqNr += 1
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		doAfterCreateSeqNr += 1
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
