package io.zeitwert.fm.test.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
class TestConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    @Qualifier("coreCodeAggregateTypeEnum")
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
        e.addItem(CodeAggregateType(e, "obj_test", "Test Object"))
        e.addItem(CodeAggregateType(e, "doc_test", "Test Order"))
    }
}

