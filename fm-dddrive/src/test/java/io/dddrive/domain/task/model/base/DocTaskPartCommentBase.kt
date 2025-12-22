package io.dddrive.domain.task.model.base // Correct package

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.base.DocPartBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

abstract class DocTaskPartCommentBase(
	task: DocTask,
	override val repository: PartRepository<DocTask, DocTaskPartComment>, // Renamed
	property: Property<*>,
	id: Int,
) : DocPartBase<DocTask>(task, repository, property, id),
	DocTaskPartComment {

	override fun doInit() {
		super.doInit()
		addBaseProperty("text", String::class.java)
		addBaseProperty("createdAt", OffsetDateTime::class.java)
	}

	override fun doAfterCreate() {
		super.doAfterCreate()
		setValueByPath("createdAt", OffsetDateTime.now())
	}

	override fun delete() {}

}
