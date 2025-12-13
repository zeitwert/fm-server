package io.zeitwert.fm.building.config

import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.zeitwert.fm.building.model.enums.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("buildingConfig")
class BuildingConfig : EnumConfigBase(), InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)

			CodeBuildingType.entries
			CodeBuildingSubType.entries
			CodeHistoricPreservation.entries
			CodeBuildingRatingStatus.entries
			CodeBuildingMaintenanceStrategy.entries
			CodeBuildingPart.Enumeration.INSTANCE
			CodeBuildingPartCatalog.entries
			CodeBuildingPriceIndex.entries
			CodeBuildingElementDescription.entries
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType(e, "obj_building", "Building"))
	}

}

