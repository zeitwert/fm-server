package io.zeitwert.fm.building.config

import dddrive.ddd.model.base.EnumConfigBase
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
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
			aggregateTypeEnum.addItem(CodeAggregateType("obj_building", "Building"))
			CodeBuildingType.entries
			CodeBuildingSubType.entries
			CodeHistoricPreservation.entries
			CodeBuildingRatingStatus.entries
			CodeBuildingMaintenanceStrategy.entries
			CodeBuildingPart.entries
			CodeBuildingPartCatalog.entries
			CodeBuildingPriceIndex.entries
			CodeBuildingElementDescription.entries
		} finally {
			endConfig()
		}
	}

}
