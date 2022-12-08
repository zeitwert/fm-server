package io.zeitwert.fm.building.service.api.impl;

import static io.zeitwert.fm.util.NumericUtils.roundProgressive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.building.service.api.dto.ProjectionElement;

@Service("projectionService")
@DependsOn("codeBuildingPriceIndexEnum")
public class ProjectionServiceImpl implements ProjectionService {

	public ProjectionResult getProjection(ObjBuilding building, int duration) {
		return this.getProjection(Set.of(building), duration);
	}

	/**
	 * - get startYear (max(building.element.conditionYear))
	 * - for all elements of all buildings:
	 * -
	 * 
	 * @param buildings
	 * @param duration
	 * @return
	 */
	public ProjectionResult getProjection(Set<ObjBuilding> buildings, int duration) {
		List<ProjectionElement> elementList = new ArrayList<>();
		Map<EnumeratedDto, ObjBuildingPartElementRating> elementMap = new HashMap<>();
		Map<String, List<ProjectionPeriod>> elementResultMap = new HashMap<>();
		int startYear = this.getMinProjectionDate(buildings);
		//@formatter:off
		for (ObjBuilding building : buildings) {
			EnumeratedDto buildingEnum = this.getAsEnumerated(building);
			for (ObjBuildingPartElementRating element : building.getCurrentRating().getElementList()) {
				EnumeratedDto elementEnum = this.getAsEnumerated(element);
				EnumeratedDto buildingPartEnum = EnumeratedDto.fromEnum(element.getBuildingPart());
				if (element.getValuePart() != null && element.getConditionYear() != null) {
					if (element.getValuePart() > 0 && element.getCondition() > 0) {
						List<ProjectionPeriod> elementPeriodList = element.getBuildingPart().getProjection(
							/* elementValue  => */ 100.0,
							/* conditionYear => */ element.getConditionYear(),
							/* condition     => */ element.getCondition() / 100.0,
							/* startYear     => */ startYear,
							/* duration      => */ duration
						);
						elementList.add(ProjectionElement.builder().building(buildingEnum).element(elementEnum).buildingPart(buildingPartEnum).build());
						elementMap.put(elementEnum, element);
						elementResultMap.put(elementEnum.getId(), elementPeriodList);
					}
				}
			}
		}
		ProjectionResult rawResult =
			ProjectionResult.builder()
				.startYear(startYear)
				.duration(duration)
				.elementList(elementList)
				.elementMap(elementMap)
				.elementResultMap(elementResultMap)
				.build();
		//@formatter:on
		return this.consolidateProjection(rawResult);
	}

	private int getMinProjectionDate(Set<ObjBuilding> buildings) {
		int projectionYear = 0;
		for (ObjBuilding building : buildings) {
			int minProjYear = this.getMinProjectionDate(building);
			if (minProjYear > projectionYear) {
				projectionYear = minProjYear;
			}
		}
		return projectionYear;
	}

	private int getMinProjectionDate(ObjBuilding building) {
		int projectionYear = building.getInsuredValueYear();
		for (ObjBuildingPartElementRating element : building.getCurrentRating().getElementList()) {
			if (element.getValuePart() != null && element.getConditionYear() != null) {
				if (element.getValuePart() > 0 && element.getConditionYear() > projectionYear) {
					projectionYear = element.getConditionYear();
				}
			}
		}
		return projectionYear;
	}

	private EnumeratedDto getAsEnumerated(ObjBuilding building) {
		String id = Integer.toString(building.getId());
		return EnumeratedDto.builder().id(id).name(building.getName()).build();
	}

	private EnumeratedDto getAsEnumerated(ObjBuildingPartElementRating element) {
		String id = Integer.toString(element.getId());
		return EnumeratedDto.builder().id(id)
				.name(element.getMeta().getAggregate().getName() + ": " + element.getBuildingPart().getName()).build();
	}

	private ProjectionResult consolidateProjection(ProjectionResult projectionResult) {

		List<ProjectionPeriod> buildingPeriodList = new ArrayList<>();

		double techPart = 0;
		for (ObjBuildingPartElementRating part : projectionResult.getElementMap().values()) {
			techPart += part.getValuePart() / 100 * part.getBuildingPart().getTechRate();
		}
		double techRate = CodeBuildingPart.getTechRate(techPart);

		for (int year = projectionResult.getStartYear(); year <= projectionResult.getEndYear(); year++) {

			double originalValue = 0;
			double timeValue = 0;
			double restorationCosts = 0;
			List<ProjectionElement> restorationElements = new ArrayList<>();

			for (EnumeratedDto elementEnum : projectionResult.getElementMap().keySet()) {
				ObjBuildingPartElementRating element = projectionResult.getElement(elementEnum);
				List<ProjectionPeriod> elementPeriods = projectionResult.getElementResultMap().get(elementEnum.getId());
				ProjectionPeriod elementPeriod = elementPeriods.get(year - elementPeriods.get(0).getYear());
				ObjBuilding building = projectionResult.getBuilding(elementEnum);
				double buildingValue = building.getBuildingValue(year);
				double elementValue = buildingValue * element.getValuePart() / 100.0;
				originalValue += elementValue;
				timeValue += elementValue * elementPeriod.getTimeValue() / 100.0;
				double elementRestorationCosts = elementValue * elementPeriod.getRestorationCosts() / 100.0;
				elementRestorationCosts = roundProgressive(elementRestorationCosts);
				restorationCosts += elementRestorationCosts;
				if (elementRestorationCosts != 0) {
					EnumeratedDto buildingEnum = this.getAsEnumerated(building);
					EnumeratedDto buildingPartEnum = EnumeratedDto.fromEnum(element.getBuildingPart());
					//@formatter:off
					ProjectionElement restorationElement = ProjectionElement.builder()
						.element(elementEnum)
						.building(buildingEnum)
						.buildingPart(buildingPartEnum)
						.restorationCosts(elementRestorationCosts)
						.build();
					restorationElements.add(restorationElement);
					//@formatter:on
				}
			}

			double maintenanceRate = techRate * CodeBuildingPart.getMaintenanceRate(timeValue / originalValue) / 100.0;
			double maintenanceCosts = maintenanceRate * originalValue;

			originalValue = roundProgressive(originalValue);
			timeValue = roundProgressive(timeValue);
			restorationCosts = roundProgressive(restorationCosts);
			maintenanceCosts = roundProgressive(maintenanceCosts);

			//@formatter:off
			ProjectionPeriod buildingPeriod =
				ProjectionPeriod.builder()
					.year(year)
					.originalValue(originalValue)
					.timeValue(timeValue)
					.restorationCosts(restorationCosts)
					.restorationElements(restorationElements)
					.techPart(techPart)
					.techRate(techRate)
					.maintenanceRate(maintenanceRate)
					.maintenanceCosts(maintenanceCosts)
					.build();
			//@formatter:on
			buildingPeriodList.add(buildingPeriod);

		}

		//@formatter:off
		return ProjectionResult.builder()
			.startYear(projectionResult.getStartYear())
			.duration(projectionResult.getDuration())
			.elementList(projectionResult.getElementList())
			.elementMap(projectionResult.getElementMap())
			.elementResultMap(projectionResult.getElementResultMap())
			.periodList(buildingPeriodList)
			.build();
		//@formatter:on

	}

}
