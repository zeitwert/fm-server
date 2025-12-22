package io.zeitwert.fm.test.config

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.ddd.model.enums.CodePartListType
import io.dddrive.ddd.model.enums.CodePartListTypeEnum
import io.dddrive.doc.model.enums.CodeCaseDef
import io.dddrive.doc.model.enums.CodeCaseDefEnum
import io.dddrive.doc.model.enums.CodeCaseStage
import io.dddrive.doc.model.enums.CodeCaseStageEnum
import io.dddrive.enums.model.base.EnumConfigBase
import io.zeitwert.fm.test.model.enums.CodeTestType
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

			// Trigger enum initialization
			CodeTestType.Enumeration
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
		e.addItem(CodeCaseStage("test.new", "test", "initial", "New", "New", 10, null, null, null))
		e.addItem(CodeCaseStage("test.open", "test", "intermediate", "Assigned", "Assigned", 20, null, null, null))
		e.addItem(
			CodeCaseStage(
				"test.progress",
				"test",
				"intermediate",
				"In Progress",
				"In Progress",
				30,
				null,
				null,
				null,
			),
		)
		e.addItem(CodeCaseStage("test.done", "test", "terminal", "Done", "Done", 40, null, null, null))
	}

}
