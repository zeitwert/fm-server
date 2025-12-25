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

	// ============================================================================
	// Delegated properties (DocMeta interface)
	// ============================================================================

	private var _caseDef: CodeCaseDef? by enumProperty()
	override val caseDef: CodeCaseDef? get() = _caseDef

	private var _caseStage: CodeCaseStage? by enumProperty()
	override val caseStage: CodeCaseStage? get() = _caseStage

	override var assignee: ObjUser? by referenceProperty()

	private val _transitionList: PartListProperty<DocPartTransition> by partListProperty()
	override val transitionList: List<DocPartTransition> get() = _transitionList.toList()

	// ============================================================================

	private var oldCaseStage: CodeCaseStage? = null

	override val meta: DocMeta
		get() = this

	override val docTypeId get() = repository.aggregateType.id

	// Trigger delegate initialization to register properties before persistence layer access.
	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		super.doInit()
		_caseDef
		_caseStage
		assignee
		_transitionList
	}

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

	override val isInWork: Boolean get() = caseStage?.isInWork ?: true

	override fun setCaseDef(caseDef: CodeCaseDef) {
		require(meta.caseDef == null) { "caseDef empty" }
		unfreeze()
		_caseDef = caseDef
	}

	override fun setCaseStage(
		newCaseStage: CodeCaseStage,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		require(!newCaseStage.isAbstract) { "valid caseStage (i)" }
		require(caseDef == null || newCaseStage.caseDef === caseDef) { "valid caseStage (ii)" }
		if (caseDef == null) {
			setCaseDef(newCaseStage.caseDef)
		}
		if (caseStage == null) { // initial transition
			_transitionList.add(null).init(userId, timestamp, caseStage, newCaseStage)
			oldCaseStage = newCaseStage
		}
		_caseStage = newCaseStage
	}

	override val caseStages: List<CodeCaseStage>
		get() = caseDef?.getCaseStages() ?: emptyList()

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === _transitionList) {
			return directory
				.getPartRepository(DocPartTransition::class.java)
				.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		_caption = caption
	}

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		//Integer orderNr = ((AggregateRepositorySPI<?>) getRepository()).getIdProvider().getOrderNr(getId());
	// 		//addSearchToken(orderNr + "");
	// 	}

}
