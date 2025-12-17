package io.dddrive.core.ddd.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartMeta
import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.ddd.model.PartSPI
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.base.EntityWithPropertiesBase
import io.dddrive.util.Invariant

abstract class PartBase<A : Aggregate>(
	override val aggregate: A,
	override val repository: PartRepository<A, out Part<A>>,
	override val parentProperty: Property<*>,
	id: Int,
) : EntityWithPropertiesBase(),
	Part<A>,
	PartMeta<A>,
	PartSPI<A> {

	protected val _id: BaseProperty<Int> = this.addBaseProperty<Int>("id", Int::class.java)

	override val isNew: Boolean = !aggregate.meta.isInLoad
	private var isCalcDisabled = 0
	private var _isInCalc = false
	private var didCalcAll = false
	private var didCalcVolatile = false

	init {
		this._id.value = id
	}

	override val directory: RepositoryDirectory
		get() = this.aggregate.meta.repository.directory

	override val id: Int
		get() {
			return this._id.value!!
		}

	override val meta: PartMeta<A>
		get() = this

	override val isInLoad: Boolean
		get() = this.aggregate.meta.isInLoad

	private fun buildPath(basePath: String): String {
		val parentProp = this.parentProperty
		if (parentProp is PartListProperty<*>) {
			var index = parentProp.getIndexOfPart(this)
			if (index == -1) {
				index = parentProp.partCount
			}
			return "$basePath[$index]"
		} else {
			return basePath + "." + this.id
		}
	}

	override val relativePath: String
		get() {
			val parentProp = this.parentProperty
			val parentRelativePath = parentProp.relativePath
			return buildPath(parentRelativePath)
		}

	override val path: String
		get() {
			val parentProp = this.parentProperty
			val parentPath = parentProp.path
			return buildPath(parentPath)
		}

	override fun fireFieldChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	) {
		(this.aggregate as EntityWithPropertiesSPI).fireFieldChange(op, path, value, oldValue, isInCalc)
	}

	override fun doLogChange(propertyName: String): Boolean = this.repository.doLogChange(propertyName)

	override fun hasPart(partId: Int): Boolean = this.aggregate.hasPart(partId)

	override fun getPart(partId: Int): Part<*> = this.aggregate.getPart(partId)

	override val isFrozen: Boolean
		get() = this.aggregate.isFrozen

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> = throw RuntimeException("did not instantiate part for property " + this.className + "." + property.name)

	override fun doBeforeSet(
		property: Property<*>,
		value: Any?,
		oldValue: Any?,
	) {
	}

	override fun doAfterSet(property: Property<*>) {
		this.calcAll()
	}

	override fun doAfterAdd(
		property: Property<*>,
		part: Part<*>?,
	) {
		if (part != null) {
			(this.aggregate as EntityWithPropertiesSPI).doAfterAdd(property, part)
		}
		this.calcAll()
	}

	override fun delete() {
	}

	override fun doAfterRemove(property: Property<*>) {
		this.calcAll()
	}

	override fun doAfterClear(property: Property<*>) {
		this.calcAll()
	}

	override fun isCalcEnabled(): Boolean = this.isCalcDisabled == 0 && this.aggregate.meta.isCalcEnabled()

	override fun disableCalc() {
		this.isCalcDisabled += 1
	}

	override fun enableCalc() {
		this.isCalcDisabled -= 1
	}

	override fun isInCalc(): Boolean = this._isInCalc

	protected fun beginCalc() {
		this._isInCalc = true
		this.didCalcAll = false
		this.didCalcVolatile = false
	}

	protected fun endCalc() {
		this._isInCalc = false
	}

	override fun calcAll() {
		if (!this.isCalcEnabled() || this.isInCalc()) {
			return
		}
		try {
			this.beginCalc()
			this.doCalcAll()
			this.aggregate.meta.calcAll()
			Invariant.assertThis(this.didCalcAll, this.className + ": doCalcAll was propagated")
		} finally {
			this.endCalc()
		}
	}

	protected fun doCalcAll() {
		this.didCalcAll = true
	}

	override fun calcVolatile() {
		if (!this.isCalcEnabled() || this.isInCalc()) {
			return
		}
		try {
			this.beginCalc()
			this.doCalcVolatile()
			Invariant.assertThis(this.didCalcVolatile, this.className + ": doCalcAll was propagated")
		} finally {
			this.endCalc()
		}
	}

	protected fun doCalcVolatile() {
		this.didCalcVolatile = true
	}

	private val className: String
		get() = this.javaClass.getSuperclass().getSimpleName()

}
