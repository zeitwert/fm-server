package dddrive.ddd.core.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartMeta
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.core.model.PartSPI
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.base.EntityWithPropertiesBase

abstract class PartBase<A : Aggregate>(
	override val aggregate: A,
	override val repository: PartRepository<A, out Part<A>>,
	override val parentProperty: Property<*>,
	override val id: Int,
) : EntityWithPropertiesBase(),
	Part<A>,
	PartMeta<A>,
	PartSPI<A> {

	override val isNew: Boolean = !aggregate.meta.isInLoad

	private var isCalcDisabled = 0
	override var isInCalc = false

	var doAfterCreateSeqNr: Int = 0
	private var didCalcAll = false
	private var didCalcVolatile = false

	override val directory: RepositoryDirectory
		get() = aggregate.meta.repository.directory

	override val meta: PartMeta<A>
		get() = this

	override val isInLoad: Boolean
		get() = aggregate.meta.isInLoad

	override fun doAfterCreate() {
		doAfterCreateSeqNr += 1
	}

	override fun fireFieldChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
	) {
		(aggregate as AggregateSPI).fireFieldChange(op, path, value, oldValue, isInCalc)
	}

	override fun doLogChange(propertyName: String): Boolean = repository.doLogChange(propertyName)

	override fun hasPart(partId: Int): Boolean = (aggregate as EntityWithPropertiesSPI).hasPart(partId)

	override fun getPart(partId: Int): Part<*> = (aggregate as EntityWithPropertiesSPI).getPart(partId)

	override val isFrozen: Boolean
		get() = (aggregate as EntityWithProperties).isFrozen

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> = throw RuntimeException("did not instantiate part for property " + className + "." + property.name)

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
			(aggregate as EntityWithPropertiesSPI).doAfterAdd(property, part)
		}
		calcAll()
	}

	override fun delete() {
	}

	override fun doAfterRemove(property: Property<*>) {
		calcAll()
	}

	override fun doAfterClear(property: Property<*>) {
		calcAll()
	}

	override val isCalcEnabled get() = isCalcDisabled == 0 && aggregate.meta.isCalcEnabled

	override fun disableCalc() {
		isCalcDisabled += 1
	}

	override fun enableCalc() {
		isCalcDisabled -= 1
	}

	protected fun beginCalc() {
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
			aggregate.meta.calcAll()
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
			check(didCalcVolatile) { "$className: doCalcAll was propagated" }
		} finally {
			endCalc()
		}
	}

	protected open fun doCalcVolatile() {
		didCalcVolatile = true
	}

	private val className: String
		get() = javaClass.simpleName

	override fun toString() = "$className[$id] $properties"

}
