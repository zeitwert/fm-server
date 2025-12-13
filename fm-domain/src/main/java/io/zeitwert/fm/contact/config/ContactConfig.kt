package io.zeitwert.fm.contact.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.ddd.model.enums.CodePartListType
import io.dddrive.core.ddd.model.enums.CodePartListTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeAddressType
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeGender
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Contact domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 */
@Component("contactConfig")
class ContactConfig : EnumConfigBase(), InitializingBean {

    @Autowired
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    @Autowired
    lateinit var partListTypeEnum: CodePartListTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            initCodeAggregateType(aggregateTypeEnum)
            initCodePartListType(partListTypeEnum)

            // Trigger enum initialization
            CodeContactRole.entries
            CodeSalutation.entries
            CodeTitle.entries
            CodeGender.entries
            CodeAddressType.entries
            CodeAddressChannel.entries
        } finally {
            endConfig()
        }
    }

    private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
        e.addItem(CodeAggregateType(e, "obj_contact", "Contact"))
    }

    private fun initCodePartListType(e: CodePartListTypeEnum) {
        e.addItem(CodePartListType(e, "contact.addressList", "Address"))
    }

}
