package io.dddrive.core.doc.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.base.AggregateBase
import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocMeta
import io.dddrive.core.doc.model.DocPartTransition
import io.dddrive.core.doc.model.DocRepository
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import java.time.OffsetDateTime

abstract class DocBase protected constructor(
	repository: DocRepository<out Doc>,
) : AggregateBase(repository),
	Doc,
	DocMeta {

	// @formatter:off
	protected val _docTypeId: BaseProperty<String> = this.addBaseProperty<String>("docTypeId", String::class.java)
	protected val _caseDef: EnumProperty<CodeCaseDef> = this.addEnumProperty<CodeCaseDef>("caseDef", CodeCaseDef::class.java)
	protected val _caseStage: EnumProperty<CodeCaseStage> = this.addEnumProperty<CodeCaseStage>("caseStage", CodeCaseStage::class.java)
	protected val _isInWork: BaseProperty<Boolean> = this.addBaseProperty<Boolean>("isInWork", Boolean::class.java)
	protected val _assignee: ReferenceProperty<ObjUser> = this.addReferenceProperty<ObjUser>("assignee", ObjUser::class.java)
	private val _transitionList = this.addPartListProperty<DocPartTransition>("transitionList", DocPartTransition::class.java)
	// @formatter:on

	private var oldCaseStage: CodeCaseStage? = null

	override val repository: DocRepository<*>
		get() = super.repository as DocRepository<*>

	override val meta: DocMeta
		get() = this

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		try {
			this.disableCalc()
			super.doCreate(aggregateId, tenantId, userId, timestamp)
			this._docTypeId.value = this.repository.aggregateType.id
		} finally {
			this.enableCalc()
		}
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		try {
			this.disableCalc()
			this._owner.id = userId
			this._version.value = 0
			this._createdByUser.id = userId
			this._createdAt.value = timestamp
		} finally {
			this.enableCalc()
		}
		// freeze until caseDef is set
		this.freeze() // TODO reconsider
	}

	override fun doAfterLoad() {
		super.doAfterLoad()
		this.oldCaseStage = this.caseStage
	}

	// 	@Override
	// 	public void doAssignParts() {
	// 		super.doAssignParts();
	// 		DocPartItemRepository itemRepository = this.getRepository().getItemRepository();
	// 		for (Property<?> property : this.getProperties()) {
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
		this._transitionList.addPart(null).init(userId, timestamp, oldCaseStage, caseStage!!)
		super.doBeforeStore(userId, timestamp)
		try {
			this.disableCalc()
			this._version.value = this._version.value!! + 1
			this._modifiedByUser.id = userId
			this._modifiedAt.value = timestamp
		} finally {
			this.enableCalc()
		}
	}

	override val isInWork: Boolean get() = this._isInWork.value!!

	override fun setCaseDef(caseDef: CodeCaseDef) {
		require(this.meta.caseDef == null) { "caseDef empty" }
		this.unfreeze()
		this._caseDef.value = caseDef
	}

	override fun setCaseStage(
		caseStage: CodeCaseStage,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		require(!caseStage.isAbstract) { "valid caseStage (i)" }
		require(this.caseDef == null || caseStage.caseDef === this.caseDef) { "valid caseStage (ii)" }
		if (this.caseDef == null) {
			this.setCaseDef(caseStage.caseDef)
		}
		if (this.caseStage == null) { // initial transition
			this._transitionList.addPart(null).init(userId, timestamp, this.caseStage, caseStage)
			this.oldCaseStage = caseStage
		}
		this._caseStage.value = caseStage
		this._isInWork.value = caseStage.isInWork
	}

	override val caseStages: List<CodeCaseStage>
		get() = this.caseDef?.getCaseStages() ?: emptyList()

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._transitionList) {
			return this.directory
				.getPartRepository(DocPartTransition::class.java)
				.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	protected fun setCaption(caption: String?) {
		this._caption.value = caption
	}

	// 	@Override
	// 	public void doCalcSearch() {
	// 		super.doCalcSearch();
	// 		//Integer orderNr = ((AggregateRepositorySPI<?>) this.getRepository()).getIdProvider().getOrderNr(this.getId());
	// 		//this.addSearchToken(orderNr + "");
	// 	}

}
