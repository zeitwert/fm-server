package io.dddrive.domain.household.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.domain.household.model.enums.CodeSalutation
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("householdConfig")
class HouseholdConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	var aggregateTypeEnum: CodeAggregateTypeEnum? = null

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum!!)
			CodeSalutation.entries
			CodeLabel.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType(e, "objHousehold", "Household"))
	}

}
