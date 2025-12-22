package io.dddrive.doc.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class DocPartTransitionBase(
	doc: Doc,
	override val repository: PartRepository<Doc, DocPartTransition>,
	property: Property<*>,
	id: Int,
) : DocPartBase<Doc>(doc, repository, property, id),
	DocPartTransition {

	override fun doInit() {
		super.doInit()
		addBaseProperty("tenantId", Any::class.java)
		addReferenceProperty("user", ObjUser::class.java)
		addBaseProperty("timestamp", OffsetDateTime::class.java)
		addEnumProperty("oldCaseStage", CodeCaseStage::class.java)
		addEnumProperty("newCaseStage", CodeCaseStage::class.java)
	}

	override fun doAfterCreate() {
		super.doAfterCreate()
		setValueByPath("tenantId", aggregate.tenantId)
	}

	override fun init(
		userId: Any,
		timestamp: OffsetDateTime,
		oldCaseStage: CodeCaseStage?,
		caseStage: CodeCaseStage,
	) {
		setValueByPath("userId", userId)
		setValueByPath("timestamp", timestamp)
		setValueByPath("oldCaseStage", oldCaseStage)
		setValueByPath("newCaseStage", caseStage)
	}

}
