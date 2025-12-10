package io.zeitwert.fm.dms.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component("dmsConfig")
class DMSConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    @Qualifier("coreCodeAggregateTypeEnum")
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            initCodeAggregateType(aggregateTypeEnum)

            CodeContentKind.entries
            CodeContentType.entries
            CodeDocumentCategory.entries
            CodeDocumentKind.entries
        } finally {
            endConfig()
        }
    }

    private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
        e.addItem(CodeAggregateType(e, "obj_document", "Document"))
    }
}

