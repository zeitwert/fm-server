package dddrive.app.doc.model.base

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.ddd.model.base.AggregateBase
import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocMeta
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.obj.model.Obj
import dddrive.ddd.model.Part
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.partListProperty
import dddrive.property.delegate.referenceIdProperty
import java.time.OffsetDateTime

abstract class DocBase(
	override val repository: DocRepository<out Doc>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	AggregateSPI,
	Doc,
	DocMeta {

	private var _docTypeId by baseProperty<String>("docTypeId")
	override val docTypeId get() = _docTypeId!!

	override var caseDef by enumProperty<CodeCaseDef>("caseDef")
	override var caseStage by enumProperty<CodeCaseStage>("caseStage")

	override var assigneeId by referenceIdProperty<Obj>("assignee")

	private val _transitionList = partListProperty<Doc, DocPartTransition>("transitionList")
	override val transitionList get() = _transitionList.toList()

	private var oldCaseStage: CodeCaseStage? = null

	override val meta: DocMeta
		get() = this

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		try {
			disableCalc()
			_docTypeId = repository.aggregateType.id
		} finally {
			enableCalc()
		}
	}

	override fun doAfterLoad() {
		super.doAfterLoad()
		oldCaseStage = caseStage
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		_transitionList.add(null).init(sessionContext.userId, sessionContext.currentTime, oldCaseStage, caseStage!!)
	}

	override val isInWork: Boolean
		get() = caseStage?.isInWork ?: true

	override fun setCaseStage(
		caseStage: CodeCaseStage,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		require(!caseStage.isAbstract) { "valid caseStage (i)" }
		require(this.caseDef == null || caseStage.caseDef === caseDef) { "valid caseStage (ii)" }
		if (this.caseDef == null) {
			unfreeze()
			this.caseDef = caseStage.caseDef
		}
		if (this.caseStage == null) { // initial transition
			_transitionList.add(null).init(userId, timestamp, this.caseStage, caseStage)
			oldCaseStage = caseStage
		}
		this.caseStage = caseStage
	}

	override val caseStages: List<CodeCaseStage>
		get() = caseDef!!.caseStages

	override fun doAddPart(
		property: dddrive.property.model.Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === _transitionList) {
			return directory.getPartRepository(DocPartTransition::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		//Integer orderNr = ((AggregateRepositorySPI<?>)
	// getRepository()).getIdProvider().getOrderNr(getId());
	// 		//addSearchToken(orderNr + "");
	// 	}

}
