package dddrive.app.doc.model.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocMeta
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.base.AggregateBase
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

abstract class DocBase(
	override val repository: DocRepository<out Doc>,
	isNew: Boolean,
) : dddrive.ddd.core.model.base.AggregateBase(repository, isNew),
	Doc,
	DocMeta {

	override var caseDef: CodeCaseDef? by _root_ide_package_.dddrive.ddd.property.delegate
		.enumProperty(this, "caseDef")
	override var caseStage: CodeCaseStage? by _root_ide_package_.dddrive.ddd.property.delegate.enumProperty(
		this,
		"caseStage",
	)
	override var assignee: ObjUser? by _root_ide_package_.dddrive.ddd.property.delegate.referenceProperty(
		this,
		"assignee",
	)

	private val _transitionList: dddrive.ddd.property.model.PartListProperty<DocPartTransition> =
		_root_ide_package_.dddrive.ddd.property.delegate
			.partListProperty(this, "transitionList")
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
		property: dddrive.ddd.property.model.Property<*>,
		partId: Int?,
	): dddrive.ddd.core.model.Part<*> {
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
