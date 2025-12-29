package dddrive.app.obj.model.base

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.ObjSPI
import dddrive.app.obj.model.impl.ObjPartTransitionImpl
import dddrive.ddd.core.model.base.AggregateRepositoryBase

abstract class ObjRepositoryBase<O : Obj>(
	intfClass: Class<out Obj>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<O>(intfClass, aggregateTypeId),
	ObjRepository<O> {

	abstract val sessionContext: SessionContext

	private var didBeforeClose = false
	private var didClose = false
	private var didAfterClose = false

	override fun registerParts() {
		this.addPart(ObjPartTransition::class.java, ::ObjPartTransitionImpl)
	}

	override fun doAfterCreate(aggregate: O) {
		super.doAfterCreate(aggregate)
		(aggregate as AggregateSPI).doAfterCreate(sessionContext)
	}

	override fun doBeforeStore(aggregate: O) {
		super.doBeforeStore(aggregate)
		(aggregate as AggregateSPI).doBeforeStore(sessionContext)
	}

	override fun close(obj: O) {
		didBeforeClose = false
		doBeforeClose(obj)
		check(didBeforeClose) { "Obj: doBeforeClose was not propagated" }

		didClose = false
		doClose(obj)
		check(didClose) { "Obj: doClose was not propagated" }

		store(obj)

		didAfterClose = false
		doAfterClose(obj)
		check(didAfterClose) { "Obj: doAfterClose was not propagated" }
	}

	protected open fun doBeforeClose(obj: O) {
		didBeforeClose = true
		val seqNr = (obj as ObjBase).doBeforeCloseSeqNr
		(obj as ObjSPI).doBeforeClose(sessionContext)
		check(obj.doBeforeCloseSeqNr > seqNr) { "Obj: doBeforeClose was not propagated" }
	}

	protected open fun doClose(obj: O) {
		didClose = true
		val seqNr = (obj as ObjBase).doCloseSeqNr
		(obj as ObjSPI).doClose(sessionContext)
		check(obj.doCloseSeqNr > seqNr) { "Obj: doClose was not propagated" }
	}

	protected open fun doAfterClose(obj: O) {
		didAfterClose = true
		val seqNr = (obj as ObjBase).doAfterCloseSeqNr
		(obj as ObjSPI).doAfterClose(sessionContext)
		check(obj.doAfterCloseSeqNr > seqNr) { "Obj: doAfterClose was not propagated" }
	}

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	companion object {

		private val NotLoggedProperties = setOf("closedByUser", "closedAt", "transitionList")
	}

}
