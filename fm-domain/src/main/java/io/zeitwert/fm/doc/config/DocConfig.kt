package io.zeitwert.fm.doc.config

import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.enums.model.base.EnumConfigBase
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("docConfig")
class DocConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("doc", "Doc (readonly)"))
	}

}
