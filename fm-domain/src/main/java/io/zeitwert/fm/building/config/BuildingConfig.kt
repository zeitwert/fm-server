package io.zeitwert.fm.building.config

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.enums.model.base.EnumConfigBase
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescription
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingPriceIndex
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("buildingConfig")
class BuildingConfig :
	EnumConfigBase(),
	InitializingBean {

	@Autowired
	lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

	override fun afterPropertiesSet() {
		try {
			startConfig()
			initCodeAggregateType(aggregateTypeEnum)

			CodeBuildingType.Enumeration
			CodeBuildingSubType.Enumeration
			CodeHistoricPreservation.Enumeration
			CodeBuildingRatingStatus.Enumeration
			CodeBuildingMaintenanceStrategy.Enumeration
			CodeBuildingPart.Enumeration
			CodeBuildingPartCatalog.Enumeration
			CodeBuildingPriceIndex.Enumeration
			CodeBuildingElementDescription.Enumeration
		} finally {
			endConfig()
		}
	}

	private fun initCodeAggregateType(e: CodeAggregateTypeEnum) {
		e.addItem(CodeAggregateType("obj_building", "Building"))
	}

}
