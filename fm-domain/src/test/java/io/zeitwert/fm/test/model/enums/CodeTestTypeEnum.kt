package io.zeitwert.fm.test.model.enums

import io.dddrive.core.enums.model.base.EnumConfigBase
import io.dddrive.core.enums.model.base.EnumerationBase
import jakarta.annotation.PostConstruct
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

/**
 * Enumeration for CodeTestType using the NEW dddrive framework.
 * Loads enum items from the code_test_type table via jOOQ.
 */
@Component("codeTestTypeEnum")
@DependsOn("flyway", "flywayInitializer")
class CodeTestTypeEnum(
    private val dslContext: DSLContext
) : EnumerationBase<CodeTestType>(CodeTestType::class.java) {

    @PostConstruct
    private fun init() {
        EnumConfig.startConfig()
        try {
            // Query code_test_type table directly since jOOQ classes may not be generated yet
            val result = dslContext.select(
                DSL.field("id", String::class.java),
                DSL.field("name", String::class.java)
            ).from(DSL.table("code_test_type")).fetch()

            for (record in result) {
                val id = record.get("id", String::class.java)
                val name = record.get("name", String::class.java)
                val testType = CodeTestType(this, id, name)
                this.addItem(testType)
            }
        } finally {
            EnumConfig.endConfig()
        }
    }

    /**
     * Helper object to access EnumConfigBase methods.
     */
    private object EnumConfig : EnumConfigBase() {
        public override fun startConfig() = super.startConfig()
        public override fun endConfig() = super.endConfig()
    }

    companion object {
        private lateinit var INSTANCE: CodeTestTypeEnum

        @JvmStatic
        fun getTestType(itemId: String): CodeTestType? = INSTANCE.getItem(itemId)
    }

    init {
        INSTANCE = this
    }
}

