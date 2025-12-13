package io.zeitwert.fm.task.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("taskConfig")
class TaskConfig : EnumConfigBase(), InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	@Autowired
	lateinit var caseDefEnum: CodeCaseDefEnum

	@Autowired
	lateinit var caseStageEnum: CodeCaseStageEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			aggregateTypeEnum.addItem(CodeAggregateType(aggregateTypeEnum, "doc_task", "Task"))
			initCodeCaseDef(caseDefEnum)
			initCodeCaseStage(caseStageEnum)

			CodeTaskPriority.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeCaseDef(e: CodeCaseDefEnum) {
		e.addItem(CodeCaseDef(e, "task", "Task Standard Process", "doc_task"))
	}

	private fun initCodeCaseStage(e: CodeCaseStageEnum) {
		e.addItem(CodeCaseStage(e, "task.new", "task", "initial", "New", "New", 10, null, null, null))
		e.addItem(CodeCaseStage(e, "task.open", "task", "intermediate", "Assigned", "Assigned", 20, null, null, null))
		e.addItem(
			CodeCaseStage(
				e,
				"task.progress",
				"task",
				"intermediate",
				"In Progress",
				"In Progress",
				30,
				null,
				null,
				null
			)
		)
		e.addItem(CodeCaseStage(e, "task.done", "task", "terminal", "Done", "Done", 40, null, null, null))
	}

}
