package io.dddrive.doc.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.base.AggregateBase
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocMeta
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.DocRepository
import io.dddrive.doc.model.enums.CodeCaseDef
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class DocBase(
	override val repository: DocRepository<out Doc>,
	isNew: Boolean,
) : AggregateBase(repository, isNew),
	Doc,
	DocMeta {

	override var caseDef: CodeCaseDef? by enumProperty(this, "caseDef")
	override var caseStage: CodeCaseStage? by enumProperty(this, "caseStage")
	override var assignee: ObjUser? by referenceProperty(this, "assignee")

	private val _transitionList: PartListProperty<DocPartTransition> by partListProperty(this, "transitionList")
	override val transitionList: List<DocPartTransition> get() = _transitionList.toList()

	private var oldCaseStage: CodeCaseStage? = null

	override val meta: DocMeta
		get() = this

	override val docTypeId
		get() = repository.aggregateType.id

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		// freeze until caseDef is set
		freeze() // TODO reconsider
	}

	override fun doAfterLoad() {
		super.doAfterLoad()
		oldCaseStage = caseStage
	}

	// 	@Override
	// 	public void doAssignParts() {
	// 		super.doAssignParts();
	// 		DocPartItemRepository itemRepository = getRepository().getItemRepository();
	// 		for (Property<?> property : getProperties()) {
	// 			if (property instanceof EnumSetProperty<?> enumSet) {
	// 				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
	// 				enumSet.loadEnums(partList);
	// 			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
	// 				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
	// 				referenceSet.loadReferences(partList);
	// 			}
	// 		}
	// 	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		_transitionList.add(null).init(userId, timestamp, oldCaseStage, caseStage!!)
		super.doBeforeStore(userId, timestamp)
		try {
			disableCalc()
			_version = version + 1
			modifiedByUserId = userId
			modifiedAt = timestamp
		} finally {
			enableCalc()
		}
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
		get() = caseDef?.getCaseStages() ?: emptyList()

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === _transitionList) {
			return directory.getPartRepository(DocPartTransition::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		//Integer orderNr = ((AggregateRepositorySPI<?>)
	// getRepository()).getIdProvider().getOrderNr(getId());
	// 		//addSearchToken(orderNr + "");
	// 	}

}
