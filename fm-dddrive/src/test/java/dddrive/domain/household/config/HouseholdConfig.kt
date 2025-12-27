package dddrive.domain.household.config

import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.enums.model.base.EnumConfigBase
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
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
		e.addItem(CodeAggregateType("objHousehold", "Household"))
	}

}
