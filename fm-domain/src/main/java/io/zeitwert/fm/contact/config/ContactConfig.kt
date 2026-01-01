package io.zeitwert.fm.contact.config

import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.core.model.enums.CodePartListType
import dddrive.ddd.core.model.enums.CodePartListTypeEnum
import dddrive.ddd.enums.model.base.EnumConfigBase
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeAddressType
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeGender
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Contact domain configuration that registers aggregate types and enums in the NEW dddrive framework.
 */
@Component("contactConfig")
class ContactConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	@Autowired
	lateinit var partListTypeEnum: CodePartListTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)
			initCodePartListType(partListTypeEnum)
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
		e.addItem(CodeAggregateType("obj_contact", "Contact"))
	}

	private fun initCodePartListType(e: CodePartListTypeEnum) {
		e.addItem(CodePartListType("contact.mailAddressList", "Address"))
		e.addItem(CodePartListType("contact.electronicAddressList", "Electronic Address"))
	}

}
