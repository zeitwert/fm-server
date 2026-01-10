package io.zeitwert.fm.task.config

import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseDefEnum
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
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
			CodeTaskPriority.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeCaseDef(e: CodeCaseDefEnum) {
		e.addItem(CodeCaseDef("task", "Task Standard Process", "doc_task"))
	}

	private fun initCodeCaseStage(e: CodeCaseStageEnum) {
		e.addItem(
			CodeCaseStage(
				id = "task.new",
				defaultName = "New",
				caseDefId = "task",
				caseStageTypeId = "initial",
				name = "New",
				description = null,
				seqNr = 10,
				abstractCaseStageId = null,
				action = null,
				availableActions = null,
			),
		)
		e.addItem(
			CodeCaseStage(
				id = "task.open",
				defaultName = "Assigned",
				caseDefId = "task",
				caseStageTypeId = "intermediate",
				name = "Assigned",
				description = null,
				seqNr = 20,
				abstractCaseStageId = null,
				action = null,
				availableActions = null,
			),
		)
		e.addItem(
			CodeCaseStage(
				id = "task.progress",
				defaultName = "In Progress",
				caseDefId = "task",
				caseStageTypeId = "intermediate",
				name = "In Progress",
				description = null,
				seqNr = 30,
				abstractCaseStageId = null,
				action = null,
				availableActions = null,
			),
		)
		e.addItem(
			CodeCaseStage(
				id = "task.done",
				defaultName = "Done",
				caseDefId = "task",
				caseStageTypeId = "terminal",
				name = "Done",
				description = null,
				seqNr = 40,
				abstractCaseStageId = null,
				action = null,
				availableActions = null,
			),
		)
	}

}
