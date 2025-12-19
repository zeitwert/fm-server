package io.zeitwert.fm.building.service.api.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.ProjectionElement;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.zeitwert.fm.util.NumericUtils.roundProgressive;

@Service("projectionService")
public class ProjectionServiceImpl implements ProjectionService {

	public ProjectionResult getProjection(ObjBuilding building, int duration) {
		return this.getProjection(Set.of(building), duration);
	}

	/**
	 * - get startYear (max(building.element.ratingYear))
	 * - for all elements of all buildings:
	 * -
	 *
	 * @param buildings
	 * @param duration
	 * @return
	 */
	@Override
	public ProjectionResult getProjection(Set<ObjBuilding> buildings, int duration) {
		List<ProjectionElement> elementList = new ArrayList<>();
		Map<EnumeratedDto, ObjBuildingPartElementRating> elementMap = new HashMap<>();
		Map<String, List<ProjectionPeriod>> elementResultMap = new HashMap<>();
		int startYear = this.getMinProjectionDate(buildings);
		for (ObjBuilding building : buildings) {
			EnumeratedDto buildingEnum = this.getAsEnumerated(building);
			if (building.currentRating != null) {
				for (ObjBuildingPartElementRating element : building.currentRating.getElementList()) {
					EnumeratedDto elementEnum = this.getAsEnumerated(element);
					EnumeratedDto buildingPartEnum = EnumeratedDto.of(element.buildingPart);
					if (element.weight != null && element.ratingYear != null) {
						if (element.weight > 0 && element.condition > 0) {
							List<ProjectionPeriod> elementPeriodList = element.buildingPart.getProjection(
									/* elementValue => */ 100.0,
									/* ratingYear => */ element.ratingYear,
									/* condition => */ element.condition / 100.0,
									/* startYear => */ startYear,
									/* duration => */ duration);
							elementList.add(ProjectionElement.builder().building(buildingEnum).element(elementEnum)
									.buildingPart(buildingPartEnum).build());
							elementMap.put(elementEnum, element);
							elementResultMap.put(elementEnum.getId(), elementPeriodList);
						}
					}
				}
			}
		}
		ProjectionResult rawResult = ProjectionResult.builder()
				.startYear(startYear)
				.duration(duration)
				.elementList(elementList)
				.elementMap(elementMap)
				.elementResultMap(elementResultMap)
				.build();
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
		int projectionYear = building.insuredValueYear;
		if (building.currentRating != null) {
			for (ObjBuildingPartElementRating element : building.currentRating.getElementList()) {
				if (element.weight != null && element.weight > 0) {
					if (element.ratingYear != null && element.ratingYear > projectionYear) {
						projectionYear = element.ratingYear;
					}
				}
			}
		}
		return projectionYear;
	}

	private EnumeratedDto getAsEnumerated(ObjBuilding building) {
		String id = building.getId().toString();
		return EnumeratedDto.of(id, building.name);
	}

	private EnumeratedDto getAsEnumerated(ObjBuildingPartElementRating element) {
		String id = Integer.toString(element.getId());
		return EnumeratedDto.of(id, element.getMeta().getAggregate().name + ": " + element.buildingPart.getName());
	}

	private ProjectionResult consolidateProjection(ProjectionResult projectionResult) {

		List<ProjectionPeriod> buildingPeriodList = new ArrayList<>();

		double techPart = 0;
		for (ObjBuildingPartElementRating part : projectionResult.getElementMap().values()) {
			techPart += part.weight / 100 * part.buildingPart.getTechRate();
		}
		double techRate = CodeBuildingPart.Enumeration.getTechRate(techPart);

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
				double elementValue = buildingValue * element.weight / 100.0;
				originalValue += elementValue;
				timeValue += elementValue * elementPeriod.getTimeValue() / 100.0;
				double elementRestorationCosts = elementValue * elementPeriod.getRestorationCosts() / 100.0;
				elementRestorationCosts = roundProgressive(elementRestorationCosts);
				restorationCosts += elementRestorationCosts;
				if (elementRestorationCosts != 0) {
					EnumeratedDto buildingEnum = this.getAsEnumerated(building);
					EnumeratedDto buildingPartEnum = EnumeratedDto.of(element.buildingPart);
					ProjectionElement restorationElement = ProjectionElement.builder()
							.element(elementEnum)
							.building(buildingEnum)
							.buildingPart(buildingPartEnum)
							.restorationCosts(elementRestorationCosts)
							.build();
					restorationElements.add(restorationElement);
				}
			}

			double maintenanceRate = techRate * CodeBuildingPart.Enumeration.getMaintenanceRate(timeValue / originalValue) / 100.0;
			double maintenanceCosts = maintenanceRate * originalValue;

			originalValue = roundProgressive(originalValue);
			timeValue = roundProgressive(timeValue);
			restorationCosts = roundProgressive(restorationCosts);
			maintenanceCosts = roundProgressive(maintenanceCosts);

			ProjectionPeriod buildingPeriod = ProjectionPeriod.builder()
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
			buildingPeriodList.add(buildingPeriod);

		}

		return ProjectionResult.builder()
				.startYear(projectionResult.getStartYear())
				.duration(projectionResult.getDuration())
				.elementList(projectionResult.getElementList())
				.elementMap(projectionResult.getElementMap())
				.elementResultMap(projectionResult.getElementResultMap())
				.periodList(buildingPeriodList)
				.build();

	}

}
