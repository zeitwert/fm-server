package io.zeitwert.fm.collaboration.config

import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.enums.model.base.EnumConfigBase
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Collaboration domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 */
@Component("collaborationConfig")
class CollaborationConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)

			// Trigger enum initialization
			CodeNoteType.Enumeration
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("obj_note", "Note"))
	}

}
