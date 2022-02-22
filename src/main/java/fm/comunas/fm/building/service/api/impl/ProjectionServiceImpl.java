package fm.comunas.fm.building.service.api.impl;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.ObjBuildingPartElement;
import fm.comunas.fm.building.model.ObjBuildingRepository;
import fm.comunas.fm.building.model.enums.CodeBuildingPart;
import fm.comunas.fm.building.service.api.ProjectionPeriod;
import fm.comunas.fm.building.service.api.ProjectionResult;
import fm.comunas.fm.building.service.api.ProjectionService;
import fm.comunas.fm.building.service.api.RestorationElement;
import fm.comunas.fm.portfolio.model.ObjPortfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("projectionService")
@DependsOn("codeBuildingPriceIndexEnum")
public class ProjectionServiceImpl implements ProjectionService {

	static final int DefaultDuration = 25;

	private final SessionInfo sessionInfo;

	private final ObjBuildingRepository buildingRepo;

	@Autowired
	public ProjectionServiceImpl(SessionInfo sessionInfo, ObjBuildingRepository buildingRepo) {
		this.sessionInfo = sessionInfo;
		this.buildingRepo = buildingRepo;
	}

	protected void require(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	public ProjectionResult getProjection(ObjPortfolio portfolio) {
		Set<ObjBuilding> buildings = portfolio.getBuildingSet().stream()
				.map(id -> this.buildingRepo.get(sessionInfo, id).get())
				.collect(Collectors.toSet());
		int startYear = this.getMinProjectionDate(buildings);
		int duration = DefaultDuration;
		ProjectionResult projectionResult = this.getProjectionDetails(buildings, startYear, duration);
		return this.consolidateProjection(projectionResult);

	}

	public ProjectionResult getProjection(ObjBuilding building) {
		int startYear = this.getMinProjectionDate(building);
		int duration = DefaultDuration;
		ProjectionResult projectionResult = this.getProjectionDetails(Set.of(building), startYear, duration);
		return this.consolidateProjection(projectionResult);
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
		for (ObjBuildingPartElement element : building.getElementList()) {
			if (element.getValuePart() > 0 && element.getConditionYear() > projectionYear) {
				projectionYear = element.getConditionYear();
			}
		}
		return projectionYear;
	}

	private ProjectionResult getProjectionDetails(Set<ObjBuilding> buildings, Integer startYear, Integer duration) {
		List<RestorationElement> elementList = new ArrayList<>();
		Map<EnumeratedDto, ObjBuildingPartElement> elementMap = new HashMap<>();
		Map<String, List<ProjectionPeriod>> elementResultMap = new HashMap<>();
		//@formatter:off
		for (ObjBuilding building : buildings) {
			for (ObjBuildingPartElement element : building.getElementList()) {
				EnumeratedDto elementEnum = this.getEnumerated(element);
				EnumeratedDto buildingEnum = this.getEnumerated(building);
				EnumeratedDto buildingPartEnum = EnumeratedDto.fromEnum(element.getBuildingPart());
				if (element.getValuePart() > 0 && element.getCondition() > 0) {
					List<ProjectionPeriod> elementPeriodList = this.getProjection(
						/* buildingPart  => */ element.getBuildingPart(),
						/* elementValue  => */ 100.0,
						/* conditionYear => */ element.getConditionYear(),
						/* condition     => */ element.getCondition() / 100.0,
						/* startYear     => */ startYear,
						/* duration      => */ duration
					);
					elementList.add(RestorationElement.builder().element(elementEnum).building(buildingEnum).buildingPart(buildingPartEnum).build());
					elementMap.put(elementEnum, element);
					elementResultMap.put(elementEnum.getId(), elementPeriodList);
				}
			}
		}
		return ProjectionResult.builder()
			.startYear(startYear)
			.duration(duration)
			.elementList(elementList)
			.elementMap(elementMap)
			.elementResultMap(elementResultMap)
			.build();
		//@formatter:on
	}

	private EnumeratedDto getEnumerated(ObjBuilding building) {
		String id = Integer.toString(building.getId());
		return EnumeratedDto.builder().id(id).name(building.getName()).build();
	}

	private EnumeratedDto getEnumerated(ObjBuildingPartElement element) {
		String id = Integer.toString(element.getId());
		return EnumeratedDto.builder().id(id)
				.name(element.getMeta().getAggregate().getName() + ": " + element.getBuildingPart().getName()).build();
	}

	private ProjectionResult consolidateProjection(ProjectionResult projectionResult) {

		List<ProjectionPeriod> buildingPeriodList = new ArrayList<>();

		for (int year = projectionResult.getStartYear(); year <= projectionResult.getEndYear(); year++) {

			double originalValue = 0;
			double timeValue = 0;
			double restorationCosts = 0;
			List<RestorationElement> restorationElements = new ArrayList<>();
			double techValue = 0;

			for (EnumeratedDto elementEnum : projectionResult.getElementMap().keySet()) {
				ObjBuildingPartElement element = projectionResult.getElement(elementEnum);
				List<ProjectionPeriod> elementPeriods = projectionResult.getElementResultMap().get(elementEnum.getId());
				ProjectionPeriod elementPeriod = elementPeriods.get(year - elementPeriods.get(0).getYear());
				ObjBuilding building = projectionResult.getBuilding(elementEnum);
				double buildingValue = building.getBuildingValue(year);
				double elementValue = buildingValue * element.getValuePart() / 100.0;
				originalValue += elementValue;
				timeValue += elementValue * elementPeriod.getTimeValue() / 100.0;
				double elementRestorationCosts = elementValue * elementPeriod.getRestorationCosts() / 100.0;
				restorationCosts += elementRestorationCosts;
				if (elementRestorationCosts != 0) {
					EnumeratedDto buildingEnum = this.getEnumerated(building);
					EnumeratedDto buildingPartEnum = EnumeratedDto.fromEnum(element.getBuildingPart());
					//@formatter:off
					RestorationElement restorationElement = RestorationElement.builder()
						.element(elementEnum)
						.building(buildingEnum)
						.buildingPart(buildingPartEnum)
						.restorationCosts(elementRestorationCosts)
						.build();
					restorationElements.add(restorationElement);
					//@formatter:on
				}
				techValue += elementValue * elementPeriod.getTimeValue() / 100.0 * getTechRate(element.getBuildingPart());
			}
			double techPart = techValue / timeValue;
			double techRate = getTechRate(techPart);
			double maintenanceRate = this.getMaintenanceRate(timeValue / originalValue) / 100.0;

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
					.maintenanceCosts(maintenanceRate * techRate * originalValue)
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

	//@formatter:off
	public ProjectionPeriod getNextRestoration(
		CodeBuildingPart buildingPart,
		double elementValue,
		int conditionYear,
		double condition
	) {
	//@formatter:on

		require(buildingPart != null, "buildingPart not null");
		Double startYear = 0.0;
		Double restorationYear = 0.0;
		Double restorationCosts = 0.0;
		if ((condition / 100) > buildingPart.getOptimalRestoreTimeValue()) {
			startYear = Math.floor(getRelativeAge(buildingPart, condition / 100));
			restorationYear = Math.floor(getRelativeAge(buildingPart, buildingPart.getOptimalRestoreTimeValue())) + 1;
			restorationCosts = buildingPart.getRestoreCostPerc() / 100 - buildingPart.getOptimalRestoreTimeValue();
		} else {
			restorationCosts = (buildingPart.getRestoreCostPerc() - condition) / 100;
		}
		int duration = Double.valueOf(restorationYear - startYear).intValue();

		return ProjectionPeriod.builder()
				.year(conditionYear + duration)
				.originalValue(elementValue)
				.timeValue(buildingPart.getOptimalRestoreTimeValue())
				.restorationCosts(Math.round(restorationCosts * elementValue))
				.build();
	}

	//@formatter:off
	@Override
	public List<ProjectionPeriod> getProjection(
		CodeBuildingPart buildingPart,
		double elementValue,
		int conditionYear,
		double condition,
		int startYear,
		int duration
	) {
	//@formatter:on

		require(buildingPart != null, "buildingPart not null");
		require(conditionYear <= startYear, "valid start year (" + conditionYear + "<=" + startYear + ")");
		require(0 <= condition && condition <= 1.0, "valid condition (0 <=" + condition + " <= 1)");
		require(duration <= 100, "duration <= 100");

		final int MaxProjectionYear = startYear + (int) Math.min(100.0, duration);
		final double RestorationTimeValue = buildingPart.getOptimalRestoreTimeValue();
		final double TotalRestorationCosts = buildingPart.getRestoreCostPerc() / 100;
		final double TimeValueAfterRestoration = buildingPart.getAfterRestoreTimeValue();
		final double RelativeAgeAfterRestoration = this.getRelativeAge(buildingPart, TimeValueAfterRestoration);

		final List<ProjectionPeriod> periodList = new ArrayList<>();

		double relativeAge = this.getRelativeAge(buildingPart, condition);
		double timeValue = condition;
		double techPart = this.getTechRate(buildingPart);
		double techRate = this.getTechRate(techPart);

		for (int simYear = conditionYear; simYear <= MaxProjectionYear; simYear++) {
			boolean needRestoration = timeValue <= RestorationTimeValue;
			double restorationCosts = 0.0;
			if (needRestoration) {
				restorationCosts = (TotalRestorationCosts - timeValue) * elementValue;
				relativeAge = RelativeAgeAfterRestoration;
			}
			double maintenanceRate = this.getMaintenanceRate(timeValue) / 100.0;
			if (simYear >= startYear) {
				//@formatter:off
				ProjectionPeriod period = ProjectionPeriod.builder()
					.year(simYear)
					.originalValue(elementValue)
					.timeValue(timeValue * elementValue)
					.restorationCosts(restorationCosts)
					.techPart(techPart)
					.techRate(techRate)
					.maintenanceRate(maintenanceRate)
					.maintenanceCosts(maintenanceRate * techRate * elementValue)
					.build();
				//@formatter:on
				periodList.add(period);
			}
			relativeAge += 1;
			timeValue = this.getTimeValue(buildingPart, relativeAge);
		}
		// Assert.isTrue(periodList.get(0).getYear() == startYear, "valid start year");
		Assert.isTrue(periodList.size() == duration + 1, "valid duration");
		return periodList;
	}

	private double getTechRate(CodeBuildingPart buildingPart) {
		List<String> fullTechRates = List.of("P6", "P7", "P8", "P54", "P55", "P9", "P50", "P51", "P10");
		List<String> halfTechRates = List.of("P12", "P60", "P61", "P62");
		String id = buildingPart.getId();
		if (fullTechRates.contains(id)) {
			return 1.0;
		} else if (halfTechRates.contains(id)) {
			return 0.5;
		}
		return 0.0;
	}

	private double getTechRate(double techPart) {
		if (techPart <= 0.09) {
			return 0.5;
		} else if (techPart >= 0.22) {
			return 1.0;
		}
		return this.getRatio(techPart, 0.09, 0.22, 0.5, 1.0);
	}

	private double getMaintenanceRate(double timeValue) {
		if (timeValue >= 1.0) {
			return 0.5;
		} else if (timeValue >= 0.93) {
			return getRatio(timeValue, 0.93, 1.0, 0.6, 0.5);
		} else if (timeValue >= 0.85) {
			return getRatio(timeValue, 0.85, 0.93, 1.1, 0.6);
		} else if (timeValue >= 0.75) {
			return getRatio(timeValue, 0.75, 0.85, 2.0, 1.1);
		} else if (timeValue >= 0.67) {
			return getRatio(timeValue, 0.67, 0.75, 2.0, 2.0);
		} else if (timeValue >= 0.60) {
			return getRatio(timeValue, 0.60, 0.67, 0.5, 2.0);
		}
		return getRatio(timeValue, 0.00, 0.60, 0.7, 0.5);
	}

	private double getRatio(double timeValue, double lowBound, double highBound, double lowValue, double highValue) {
		return lowValue + (timeValue - lowBound) / (highBound - lowBound) * (highValue - lowValue);
	}

	public double getTimeValue(CodeBuildingPart buildingPart, double relativeAge) {
		if (buildingPart.getLinearDuration() > 0 && relativeAge <= buildingPart.getLinearDuration()) {
			return 1 - relativeAge / buildingPart.getLinearDuration() * (1 - buildingPart.getLinearTimeValue());
		}
		//@formatter:off
		return
			buildingPart.getC0() +
			buildingPart.getC1() * relativeAge +
			buildingPart.getC2() * Math.pow(relativeAge, 2) +
			buildingPart.getC3() * Math.pow(relativeAge, 3) +
			buildingPart.getC4() * Math.pow(relativeAge, 4) +
			buildingPart.getC5() * Math.pow(relativeAge, 5) +
			buildingPart.getC6() * Math.pow(relativeAge, 6) +
			buildingPart.getC7() * Math.pow(relativeAge, 7) +
			buildingPart.getC8() * Math.pow(relativeAge, 8) +
			buildingPart.getC9() * Math.pow(relativeAge, 9) +
			buildingPart.getC10() * Math.pow(relativeAge, 10);
		//@formatter:on
	}

	public double getRelativeAge(CodeBuildingPart buildingPart, double timeValue) {
		if (timeValue > buildingPart.getLinearTimeValue()) {
			return (1 - timeValue) / (1 - buildingPart.getLinearTimeValue()) * buildingPart.getLinearDuration();
		}
		final double PRECISION = 0.0001;
		double prevT;
		double t;
		int i = 0;
		t = buildingPart.getOptimalRestoreDuration();
		prevT = t;
		while (Math.abs(this.getTimeValue(buildingPart, t) - timeValue) > PRECISION && i < 10) {
			t = prevT - (this.getTimeValue(buildingPart, prevT) - timeValue) / fDerivative(buildingPart, prevT);
			i += 1;
			prevT = t;
		}
		return t;
	}

	public Integer getLifetime(CodeBuildingPart buildingPart, double timeValue) {
		double optimalTimeValue = buildingPart.getOptimalRestoreTimeValue();
		if (timeValue <= optimalTimeValue) {
			return 0;
		}
		return (int) Math
				.floor(this.getRelativeAge(buildingPart, optimalTimeValue) - this.getRelativeAge(buildingPart, timeValue)) + 1;
	}

	private double fDerivative(CodeBuildingPart buildingPart, double relativeAge) {
		//@formatter:off
		return
			buildingPart.getC1() +
			2 * buildingPart.getC2() * relativeAge +
			3 * buildingPart.getC3() * Math.pow(relativeAge, 2) +
			4 * buildingPart.getC4() * Math.pow(relativeAge, 3) +
			5 * buildingPart.getC5() * Math.pow(relativeAge, 4) +
			6 * buildingPart.getC6() * Math.pow(relativeAge, 5) +
			7 * buildingPart.getC7() * Math.pow(relativeAge, 6) +
			8 * buildingPart.getC8() * Math.pow(relativeAge, 7) +
			9 * buildingPart.getC9() * Math.pow(relativeAge, 8) +
			10 * buildingPart.getC10() * Math.pow(relativeAge, 9);
		//@formatter:on
	}

}
