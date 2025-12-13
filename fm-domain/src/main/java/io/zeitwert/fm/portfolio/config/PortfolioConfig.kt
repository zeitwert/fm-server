package io.zeitwert.fm.portfolio.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component("portfolioConfig")
class PortfolioConfig : EnumConfigBase(), InitializingBean {

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
        e.addItem(CodeAggregateType(e, "obj_portfolio", "Portfolio"))
    }
}

