package io.zeitwert.fm.oe.config

import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.fm.oe.model.enums.CodeLocale
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * OE domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 *
 * This follows the dfp-app-server pattern where domain-specific config classes
 * register their aggregate types via InitializingBean.afterPropertiesSet().
 */
@Component("oeConfig")
class OEConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)
			CodeUserRole.entries
			CodeTenantType.entries
			CodeCountry.entries
			CodeLocale.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("obj_user", "User"))
		e.addItem(CodeAggregateType("obj_tenant", "Tenant"))
	}

}
