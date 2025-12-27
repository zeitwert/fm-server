package dddrive.app.doc.model.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocMeta
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.validation.model.AggregatePartValidation
import dddrive.app.validation.model.enums.CodeValidationLevel
import dddrive.app.validation.model.impl.AggregatePartValidationImpl
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

abstract class DocBase(
	override val repository: DocRepository<out Doc>,
	isNew: Boolean,
) : dddrive.ddd.core.model.base.AggregateBase(repository, isNew),
	Doc,
	DocMeta {

	protected var _tenantId: Any? by baseProperty(this, "tenantId")
	override val tenantId: Any get() = _tenantId!!

	override var ownerId: Any? by baseProperty(this, "ownerId")

	protected var _createdByUserId: Any? by baseProperty<Any>(this, "createdByUserId")
	override val createdByUserId: Any get() = _createdByUserId!!

	protected var _createdAt: OffsetDateTime? by baseProperty(this, "createdAt")
	override val createdAt: OffsetDateTime get() = _createdAt!!

	override var modifiedByUserId: Any? by baseProperty(this, "modifiedByUserId")
	override var modifiedAt: OffsetDateTime? by baseProperty(this, "modifiedAt")

	protected var _caption: String? by baseProperty(this, "caption")
	override val caption: String get() = _caption ?: ""

	override var caseDef: CodeCaseDef? by enumProperty(this, "caseDef")
	override var caseStage: CodeCaseStage? by enumProperty(this, "caseStage")

	override var assigneeId: Any? by baseProperty(this, "assigneeId")

	private val _transitionList: PartListProperty<DocPartTransition> = partListProperty(this, "transitionList")
	override val transitionList: List<DocPartTransition> get() = _transitionList.toList()

	override val validationList: MutableList<AggregatePartValidation> = mutableListOf()

	private var oldCaseStage: CodeCaseStage? = null

	override val meta: DocMeta
		get() = this

	override val docTypeId
		get() = repository.aggregateType.id

	override fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	) {
		super.doCreate(aggregateId, tenantId)
		_tenantId = tenantId
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		ownerId = userId
		_createdByUserId = userId
		_createdAt = timestamp
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

	override fun beginCalc() {
		super.beginCalc()
		clearValidationList()
	}

	private fun clearValidationList() {
		validationList.clear()
	}

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
		validationList.add(AggregatePartValidationImpl(validationList.size, validationLevel, validation, path))
	}

	override fun toString(): String = caption

}
