package io.zeitwert.fm.collaboration.config

import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
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
			aggregateTypeEnum.addItem(CodeAggregateType("obj_note", "Note"))
			CodeNoteType.entries
		} finally {
			endConfig()
		}
	}

}
