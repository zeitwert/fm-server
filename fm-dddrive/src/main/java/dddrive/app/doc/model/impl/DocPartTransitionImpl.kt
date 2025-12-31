package dddrive.app.doc.model.impl

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.base.DocPartBase
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime

class DocPartTransitionImpl(
	doc: Doc,
	override val repository: PartRepository<Doc, DocPartTransition>,
	property: Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	// seqNr is the part id
	override val seqNr: Int get() = id

	private var _tenantId by baseProperty<Any>("tenantId")
	private var _userId by baseProperty<Any>("userId")
	override val userId get() = _userId!!

	private var _timestamp by baseProperty<OffsetDateTime>("timestamp")
	override val timestamp get() = _timestamp!!

	private var _oldCaseStage by enumProperty<CodeCaseStage>("oldCaseStage")
	override val oldCaseStage get() = _oldCaseStage

	private var _newCaseStage by enumProperty<CodeCaseStage>("newCaseStage")
	override val newCaseStage get() = _newCaseStage!!

	override fun doAfterCreate() {
		super.doAfterCreate()
		_tenantId = aggregate.tenantId
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
		oldCaseStage: CodeCaseStage?,
		caseStage: CodeCaseStage,
	) {
		_userId = userId
		_timestamp = timestamp
		_oldCaseStage = oldCaseStage
		_newCaseStage = caseStage
	}

}
