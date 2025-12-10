package io.zeitwert.fm.account.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Account domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 */
@Component("accountConfig")
class AccountConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    @Qualifier("coreCodeAggregateTypeEnum")
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            initCodeAggregateType(aggregateTypeEnum)

            // Trigger enum initialization
            CodeAccountType.entries
            CodeClientSegment.entries
            CodeCurrency.entries
        } finally {
            endConfig()
        }
    }

    private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
        e.addItem(CodeAggregateType(e, "obj_account", "Account"))
    }
}

