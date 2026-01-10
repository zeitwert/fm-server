package io.zeitwert.fm.account.config

import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Account domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 */
@Component("accountConfig")
class AccountConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			aggregateTypeEnum.addItem(CodeAggregateType("obj_account", "Account"))
			CodeAccountType.entries
			CodeClientSegment.entries
			CodeCurrency.entries
		} finally {
			endConfig()
		}
	}

}
