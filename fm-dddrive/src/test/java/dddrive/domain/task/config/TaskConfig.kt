package dddrive.domain.task.config

import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseDefEnum
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.enums.model.base.EnumConfigBase
import dddrive.domain.task.model.enums.CodeTaskPriority
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration("taskConfig")
open class TaskConfig :
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
			initCodeAggregateType(aggregateTypeEnum)
			initTaskCaseDefinitions(caseDefEnum, caseStageEnum)
			CodeTaskPriority.entries // Initialize priority enum
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("docTask", "Task Document"))
	}

	private fun initTaskCaseDefinitions(
		defEnum: CodeCaseDefEnum,
		stageEnum: CodeCaseStageEnum,
	) {
		// Define a simple task workflow
		val simpleTaskDef = CodeCaseDef("simpleTask", "Simple Task Workflow", "docTask")
		defEnum.addItem(simpleTaskDef)

		// Define stages for simple task workflow
		val newStage = CodeCaseStage(
			id = "task.new",
			defaultName = "New",
			caseDefId = "simpleTask",
			caseStageTypeId = "initial",
			name = "New",
			description = "Task is newly created",
			seqNr = 1,
			abstractCaseStageId = null,
			action = "CREATE",
			availableActions = listOf("START", "DONE"),
		)
		stageEnum.addItem(newStage)
		simpleTaskDef.addCaseStage(newStage)

		val inProgressStage = CodeCaseStage(
			id = "task.inProgress",
			defaultName = "In Progress",
			caseDefId = "simpleTask",
			caseStageTypeId = "active",
			name = "In Progress",
			description = "Task is being worked on",
			seqNr = 2,
			abstractCaseStageId = null,
			action = "START",
			availableActions = listOf("DONE"),
		)
		stageEnum.addItem(inProgressStage)
		simpleTaskDef.addCaseStage(inProgressStage)

		val doneStage = CodeCaseStage(
			id = "task.done",
			defaultName = "Done",
			caseDefId = "simpleTask",
			caseStageTypeId = "terminal",
			name = "Done",
			description = "Task has been completed",
			seqNr = 3,
			abstractCaseStageId = null,
			action = "DONE",
			availableActions = listOf(),
		)
		stageEnum.addItem(doneStage)
		simpleTaskDef.addCaseStage(doneStage)
	}

}
