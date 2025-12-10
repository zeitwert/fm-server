package io.zeitwert.fm.collaboration.model.enums

import io.dddrive.core.enums.model.base.EnumConfigBase
import io.dddrive.core.enums.model.base.EnumerationBase
import io.zeitwert.fm.collaboration.model.db.Tables
import jakarta.annotation.PostConstruct
import org.jooq.DSLContext
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

/**
 * Enumeration for CodeNoteType using the NEW dddrive framework.
 * Loads enum items from the database via jOOQ.
 */
@Component("codeNoteTypeEnum")
@DependsOn("flyway", "flywayInitializer")
class CodeNoteTypeEnum(
    private val dslContext: DSLContext
) : EnumerationBase<CodeNoteType>(CodeNoteType::class.java) {

    @PostConstruct
    private fun init() {
        EnumConfig.startConfig()
        try {
            for (record in dslContext.selectFrom(Tables.CODE_NOTE_TYPE).fetch()) {
                val noteType = CodeNoteType(this, record.id, record.name)
                this.addItem(noteType)
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
        private lateinit var INSTANCE: CodeNoteTypeEnum

        @JvmStatic
        fun getNoteType(itemId: String): CodeNoteType? = INSTANCE.getItem(itemId)
    }

    init {
        INSTANCE = this
    }
}

