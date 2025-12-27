package dddrive.app.doc.model.impl

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.base.DocPartBase
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.model.Property
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

class DocPartTransitionImpl(
	doc: Doc,
	override val repository: dddrive.ddd.core.model.PartRepository<Doc, DocPartTransition>,
	property: dddrive.ddd.property.model.Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	// seqNr is the part id
	override val seqNr: Int get() = id

	// Private mutable backing for read-only interface properties
	private var _tenantId: Any? by _root_ide_package_.dddrive.ddd.property.delegate
		.baseProperty(this, "tenantId")
	private var _user: ObjUser? by _root_ide_package_.dddrive.ddd.property.delegate
		.referenceProperty(this, "user")
	private var _userId: Any? by _root_ide_package_.dddrive.ddd.property.delegate.referenceIdProperty<ObjUser>(
		this,
		"user",
	)
	private var _timestamp: OffsetDateTime? by _root_ide_package_.dddrive.ddd.property.delegate.baseProperty(
		this,
		"timestamp",
	)
	private var _oldCaseStage: CodeCaseStage? by _root_ide_package_.dddrive.ddd.property.delegate.enumProperty(
		this,
		"oldCaseStage",
	)
	private var _newCaseStage: CodeCaseStage? by _root_ide_package_.dddrive.ddd.property.delegate.enumProperty(
		this,
		"newCaseStage",
	)

	override val user: ObjUser get() = _user!!
	override val timestamp: OffsetDateTime get() = _timestamp!!
	override val oldCaseStage: CodeCaseStage? get() = _oldCaseStage
	override val newCaseStage: CodeCaseStage get() = _newCaseStage!!

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
