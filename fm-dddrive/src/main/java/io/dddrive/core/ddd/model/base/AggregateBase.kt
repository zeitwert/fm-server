package io.dddrive.core.ddd.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.AggregateSPI
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.PropertyChangeListener
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.core.validation.model.AggregatePartValidation
import io.dddrive.core.validation.model.enums.CodeValidationLevel
import io.dddrive.core.validation.model.impl.AggregatePartValidationImpl
import java.time.OffsetDateTime
import java.util.function.Consumer

/** A DDD Aggregate */
abstract class AggregateBase
	protected constructor(
		repository: AggregateRepository<out Aggregate>,
	) : AggregateWithRepositoryBase(repository),
		Aggregate,
		AggregateMeta,
		AggregateSPI {

		// @formatter:off
		protected val _id: BaseProperty<Any> = addBaseProperty<Any>("id", Any::class.java)
		protected val _maxPartId: BaseProperty<Int> = addBaseProperty<Int>("maxPartId", Int::class.java)
		protected val _version: BaseProperty<Int> = addBaseProperty<Int>("version", Int::class.java)
		protected val _tenant: ReferenceProperty<ObjTenant> = addReferenceProperty<ObjTenant>("tenant", ObjTenant::class.java)
		protected val _owner: ReferenceProperty<ObjUser> = addReferenceProperty<ObjUser>("owner", ObjUser::class.java)
		protected val _caption: BaseProperty<String> = addBaseProperty<String>("caption", String::class.java)
		protected val _createdByUser: ReferenceProperty<ObjUser> = addReferenceProperty<ObjUser>("createdByUser", ObjUser::class.java)
		protected val _createdAt: BaseProperty<OffsetDateTime> = addBaseProperty<OffsetDateTime>("createdAt", OffsetDateTime::class.java)
		protected val _modifiedByUser: ReferenceProperty<ObjUser> = addReferenceProperty<ObjUser>("modifiedByUser", ObjUser::class.java)
		protected val _modifiedAt: BaseProperty<OffsetDateTime> = addBaseProperty<OffsetDateTime>("modifiedAt", OffsetDateTime::class.java)
		// @formatter:on

		private val _propertyChangeListeners: MutableSet<PropertyChangeListener> = mutableSetOf()
		private val _validations: MutableList<AggregatePartValidation> = mutableListOf()

		var doCreateSeqNr: Int = 0

		var doAfterCreateSeqNr: Int = 0

		var doAfterLoadSeqNr: Int = 0

		var doBeforeStoreSeqNr: Int = 0

		var doAfterStoreSeqNr: Int = 0
		private var _isFrozen = false
		private var _isInLoad = false
		private var isCalcDisabled = 0
		private var _isInCalc = false
		private var didCalcAll = false
		private var didCalcVolatile = false

		override fun toString(): String = caption

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
			synchronized(_maxPartId) {
				val maxPartId = _maxPartId.value
				_maxPartId.value = (maxPartId ?: 0) + 1
				return _maxPartId.value!!
			}
		}

		override fun doCreate(
			aggregateId: Any,
			tenantId: Any,
			userId: Any,
			timestamp: OffsetDateTime,
		) {
			fireEntityAddedChange(aggregateId)
			_id.value = aggregateId
			_tenant.id = tenantId
			_createdByUser.id = userId
			_createdAt.value = timestamp
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
	}
