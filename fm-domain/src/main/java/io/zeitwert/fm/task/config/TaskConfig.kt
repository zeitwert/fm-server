package io.zeitwert.fm.task.config

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.doc.model.enums.CodeCaseDef
import io.dddrive.doc.model.enums.CodeCaseDefEnum
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.doc.model.enums.CodeCaseStageEnum
import io.dddrive.enums.model.base.EnumConfigBase
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("taskConfig")
class TaskConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	@Autowired
	lateinit var caseDefEnum: CodeCaseDefEnum

	@Autowired
	lateinit var caseStageEnum: CodeCaseStageEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			aggregateTypeEnum.addItem(CodeAggregateType("doc_task", "Task"))
			initCodeCaseDef(caseDefEnum)
			initCodeCaseStage(caseStageEnum)

			CodeTaskPriority.Enumeration
		} finally {
			endConfig()
		}
	}

	private fun initCodeCaseDef(e: CodeCaseDefEnum) {
		e.addItem(CodeCaseDef("task", "Task Standard Process", "doc_task"))
	}

	private fun initCodeCaseStage(e: CodeCaseStageEnum) {
		e.addItem(CodeCaseStage("task.new", "task", "initial", "New", "New", 10, null, null, null))
		e.addItem(CodeCaseStage("task.open", "task", "intermediate", "Assigned", "Assigned", 20, null, null, null))
		e.addItem(
			CodeCaseStage("task.progress", "task", "intermediate", "In Progress", "In Progress", 30, null, null, null),
		)
		e.addItem(CodeCaseStage("task.done", "task", "terminal", "Done", "Done", 40, null, null, null))
	}

}
