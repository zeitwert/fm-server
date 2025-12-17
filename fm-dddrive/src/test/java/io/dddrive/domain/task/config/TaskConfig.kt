package io.dddrive.domain.task.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.dddrive.domain.task.model.enums.CodeTaskPriority
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
			"task.new",
			"simpleTask",
			"initial",
			"New",
			"Task is newly created",
			1,
			null,
			"CREATE",
			listOf("START", "DONE"),
		)
		stageEnum.addItem(newStage)
		simpleTaskDef.addCaseStage(newStage)

		val inProgressStage = CodeCaseStage(
			"task.inProgress",
			"simpleTask",
			"active",
			"In Progress",
			"Task is being worked on",
			2,
			null,
			"START",
			listOf("DONE"),
		)
		stageEnum.addItem(inProgressStage)
		simpleTaskDef.addCaseStage(inProgressStage)

		val doneStage = CodeCaseStage(
			"task.done",
			"simpleTask",
			"terminal",
			"Done",
			"Task has been completed",
			3,
			null,
			"DONE",
			listOf(),
		)
		stageEnum.addItem(doneStage)
		simpleTaskDef.addCaseStage(doneStage)
	}

}
