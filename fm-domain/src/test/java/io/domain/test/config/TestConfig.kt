package io.domain.test.config

import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseDefEnum
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.model.enums.CodePartListType
import dddrive.ddd.model.enums.CodePartListTypeEnum
import io.domain.test.model.enums.CodeTestType
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Test configuration that registers test aggregate types in the NEW dddrive framework.
 *
 * This follows the dfp-app-server pattern where domain-specific config classes
 * register their aggregate types via InitializingBean.afterPropertiesSet().
 *
 * The test aggregate types are also defined in R__1099_test_config.sql for database
 * reference data (intentional duplication for the dual-framework approach).
 */
@Component("testConfig")
class TestConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	@Autowired
	lateinit var partListTypeEnum: CodePartListTypeEnum

	@Autowired
	lateinit var caseDefEnum: CodeCaseDefEnum

	@Autowired
	lateinit var caseStageEnum: CodeCaseStageEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)
			initCodePartListType(partListTypeEnum)
			initCodeCaseDef(caseDefEnum)
			initCodeCaseStage(caseStageEnum)
			CodeTestType.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("obj_test", "Test Object"))
		e.addItem(CodeAggregateType("doc_test", "Test Order"))
	}

	private fun initCodePartListType(e: CodePartListTypeEnum) {
		e.addItem(CodePartListType("test.nodeList", "TestNode List"))
		e.addItem(CodePartListType("test.testTypeSet", "Test Type Set"))
	}

	private fun initCodeCaseDef(e: CodeCaseDefEnum) {
		e.addItem(CodeCaseDef("test", "Test Process", "doc_test"))
	}

	private fun initCodeCaseStage(e: CodeCaseStageEnum) {
		e.addItem(
			CodeCaseStage(
				id = "test.new",
				defaultName = "New",
				caseDefId = "test",
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
				id = "test.open",
				defaultName = "Assigned",
				caseDefId = "test",
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
				id = "test.progress",
				defaultName = "In Progress",
				caseDefId = "test",
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
				id = "test.done",
				defaultName = "Done",
				caseDefId = "test",
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
