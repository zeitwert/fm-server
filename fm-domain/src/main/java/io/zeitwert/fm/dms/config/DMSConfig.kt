package io.zeitwert.fm.dms.config

import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("dmsConfig")
class DMSConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			aggregateTypeEnum.addItem(CodeAggregateType("obj_document", "Document"))
			CodeContentKind.entries
			CodeContentType.entries
			CodeDocumentCategory.entries
			CodeDocumentKind.entries
		} finally {
			endConfig()
		}
	}

}
