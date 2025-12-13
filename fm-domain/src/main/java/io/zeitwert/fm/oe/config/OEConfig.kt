package io.zeitwert.fm.oe.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.fm.oe.model.enums.CodeLocale
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * OE domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 *
 * This follows the dfp-app-server pattern where domain-specific config classes
 * register their aggregate types via InitializingBean.afterPropertiesSet().
 */
@Component("oeConfig")
class OEConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            initCodeAggregateType(aggregateTypeEnum)

            // Trigger enum initialization
            CodeUserRole.entries
            CodeTenantType.entries
            CodeCountry.entries
            CodeLocale.entries
        } finally {
            endConfig()
        }
    }

    private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
        e.addItem(CodeAggregateType(e, "obj_user", "User"))
        e.addItem(CodeAggregateType(e, "obj_tenant", "Tenant"))
    }
}
