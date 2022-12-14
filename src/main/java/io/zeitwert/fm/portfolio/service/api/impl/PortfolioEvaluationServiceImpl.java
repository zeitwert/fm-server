package io.zeitwert.fm.portfolio.service.api.impl;

import static io.zeitwert.fm.util.NumericUtils.roundProgressive;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.util.Check;
import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationBuilding;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationParameter;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.building.service.api.dto.ProjectionElement;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.service.api.PortfolioEvaluationService;
import io.zeitwert.fm.portfolio.service.api.dto.PortfolioEvaluationResult;
import io.zeitwert.fm.util.NumericUtils;

@Service("portfolioEvaluationService")
public class PortfolioEvaluationServiceImpl implements PortfolioEvaluationService {

	public static final String SOFT_RETURN = "\u000B";

	public static final Color VERY_BAD_CONDITION = new Color(229, 79, 41);
	public static final Color BAD_CONDITION = new Color(250, 167, 36);
	public static final Color OK_CONDITION = new Color(120, 192, 107);
	public static final Color GOOD_CONDITION = new Color(51, 135, 33);

	private final CodeBuildingPartEnum buildingPartEnum;
	private final ObjBuildingCache buildingCache;
	private final ProjectionService projectionService;

	public PortfolioEvaluationServiceImpl(ObjBuildingCache buildingCache, ProjectionService projectionService,
			CodeBuildingPartEnum buildingPartEnum) {
		this.buildingCache = buildingCache;
		this.projectionService = projectionService;
		this.buildingPartEnum = buildingPartEnum;
	}

	@Override
	public PortfolioEvaluationResult getEvaluation(ObjPortfolio portfolio) {

		Formatter fmt = Formatter.INSTANCE;

		Set<ObjBuilding> buildings = portfolio.getBuildingSet().stream().map(id -> this.buildingCache.get(id))
				.collect(Collectors.toSet());
		ProjectionResult projectionResult = this.projectionService.getProjection(buildings,
				ProjectionService.DefaultDuration);

		List<EvaluationBuilding> buildingList = new ArrayList<>();
		Integer maxInsuredValue = buildings.stream().map(b -> b.getInsuredValue().intValue()).reduce(0,
				(max, b) -> b > max ? b : max);
		for (ObjBuilding building : buildings) {
			ObjBuildingPartRating rating = building.getCurrentRating();
			Integer ratingYear = rating != null ? rating.getRatingDate().getYear() : null;
			EvaluationBuilding evaluationBuilding = EvaluationBuilding.builder()
					.id(building.getId())
					.name(building.getName())
					.description(building.getDescription())
					.buildingNr(building.getBuildingNr())
					.address(building.getStreet() + " " + building.getZip() + " " + building.getCity())
					.insuredValue(building.getInsuredValue().intValue())
					.relativeValue((int) (building.getInsuredValue().intValue() / maxInsuredValue.doubleValue() * 100.0))
					.insuredValueYear(building.getInsuredValueYear())
					.ratingYear(ratingYear)
					.condition(building.getCondition(2022))
					.conditionColor(this.getConditionColor(building.getCondition(2022)))
					.build();
			buildingList.add(evaluationBuilding);
		}
		buildingList.sort((b1, b2) -> b1.compareTo(b2));

		String timeValue = fmt.formatMonetaryValue(projectionResult.getPeriodList().get(0).getTimeValue(), "CHF");
		String shortTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 0, 1), "CHF");
		String midTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 2, 5), "CHF");
		String longTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 6, 25), "CHF");
		String averageMaintenance = fmt.formatMonetaryValue(this.getAverageMaintenanceCosts(projectionResult, 1, 5), "CHF");

		// List of evaluation elements, grouped by building part, with summed up
		// restoration costs
		int currentYear = LocalDate.now().getYear();
		Map<String, EvaluationElement> elementMap = new HashMap<>();
		for (ProjectionPeriod period : projectionResult.getPeriodList()) {
			for (ProjectionElement element : period.getRestorationElements()) {
				EvaluationElement ee = elementMap.get(element.getBuildingPart().getId());
				if (ee == null) {
					ee = EvaluationElement.builder()
							.name(element.getBuildingPart().getName())
							.shortTermCosts(0)
							.midTermCosts(0)
							.longTermCosts(0)
							.build();
					elementMap.put(element.getBuildingPart().getId(), ee);
				}
				if (period.getYear() <= currentYear + 1) {
					ee.setShortTermCosts(ee.getShortTermCosts() + (int) element.getRestorationCosts());
				} else if (period.getYear() <= currentYear + 4) {
					ee.setMidTermCosts(ee.getMidTermCosts() + (int) element.getRestorationCosts());
				} else if (period.getYear() <= currentYear + 25) {
					ee.setLongTermCosts(ee.getLongTermCosts() + (int) element.getRestorationCosts());
				}
			}
		}

		List<EvaluationElement> elements = new ArrayList<>();
		for (CodeBuildingPart buildingPart : this.buildingPartEnum.getItems()) {
			EvaluationElement ee = elementMap.get(buildingPart.getId());
			if (ee != null) {
				elements.add(ee);
			}
		}

		// Integer ratingYear = 9999;
		// int elementCount = 0;
		// int totalWeight = 0;
		int totalRating = 0;
		// for (ObjBuildingPartElementRating element :
		// portfolio.getCurrentRating().getElementList()) {
		// if (element.getWeight() != null && element.getWeight() > 0) {
		// String description = this.replaceEol(element.getDescription());
		// if (!StringUtils.isEmpty(element.getConditionDescription())) {
		// description += "<br/><b>Zustand</b>: " + element.getConditionDescription();
		// }
		// if (!StringUtils.isEmpty(element.getMeasureDescription())) {
		// description += "<br/><b>Massnahmen</b>: " + element.getMeasureDescription();
		// }
		// EvaluationElement dto = EvaluationElement.builder()
		// .name(element.getBuildingPart().getName())
		// .description(description)
		// .weight(element.getWeight())
		// .rating(element.getCondition())
		// .ratingColor(this.getRatingColor(element.getCondition()))
		// .restorationYear(this.getRestorationYear(projectionResult,
		// element.getBuildingPart()))
		// .restorationCosts(this.getRestorationCosts(projectionResult,
		// element.getBuildingPart()))
		// .build();
		// elements.add(dto);
		// if (element.getRatingYear() < ratingYear) {
		// ratingYear = element.getRatingYear();
		// }
		// elementCount += 1;
		// totalWeight += element.getWeight();
		// totalRating += element.getCondition();
		// }
		// }

		// totalRating = (int) Math.round(totalRating / elementCount);
		// EvaluationElement dto = EvaluationElement.builder()
		// .name("Total")
		// .weight(totalWeight)
		// .rating(totalRating)
		// .ratingColor(this.getRatingColor(totalRating))
		// .build();
		// elements.add(dto);

		List<EvaluationPeriod> periods = new ArrayList<>();
		int aggrCosts = 0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int totalCosts = (int) (pp.getMaintenanceCosts() + pp.getRestorationCosts());
			aggrCosts += totalCosts;
			String restorationElement = "";
			if (pp.getRestorationElements().size() == 1) {
				restorationElement = pp.getRestorationElements().get(0).getBuildingPart().getName();
			}
			EvaluationPeriod eps = EvaluationPeriod.builder()
					.year(pp.getYear())
					.originalValue((int) pp.getOriginalValue())
					.timeValue((int) pp.getTimeValue())
					.maintenanceCosts((int) pp.getMaintenanceCosts())
					.restorationCosts((int) pp.getRestorationCosts())
					.restorationElement(restorationElement)
					.restorationBuilding("")
					.totalCosts(totalCosts)
					.aggrCosts(aggrCosts)
					.build();
			periods.add(eps);
			if (pp.getRestorationElements().size() > 1) {
				for (ProjectionElement re : pp.getRestorationElements()) {
					EvaluationPeriod epd = EvaluationPeriod.builder()
							.restorationBuilding(re.getBuilding().getName())
							.restorationElement(re.getBuildingPart().getName())
							.restorationCosts((int) re.getRestorationCosts())
							.build();
					periods.add(epd);
				}
			}
		}

		return PortfolioEvaluationResult.builder().id(portfolio.getId()).name(portfolio.getName())
				.description(this.replaceEol(portfolio.getDescription())).accountName(portfolio.getAccount().getName())
				.buildings(buildingList).elements(elements).startYear(projectionResult.getStartYear()).periods(periods).build();
	}

	private String replaceEol(String text) {
		return text != null ? text.replace("<br>", PortfolioEvaluationServiceImpl.SOFT_RETURN) : "";
	}

	private int getAverageMaintenanceCosts(ProjectionResult projectionResult, int startYear, int endYear) {
		Check.requireThis(startYear <= endYear, "valid years");
		double costs = 0.0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int yearSinceStart = pp.getYear() - projectionResult.getStartYear();
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.getMaintenanceCosts();
			}
		}
		return (int) NumericUtils.roundProgressive(costs / (endYear - startYear + 1));
	}

	private int getRestorationCosts(ProjectionResult projectionResult, int startYear, int endYear) {
		Check.requireThis(startYear <= endYear, "valid years");
		double costs = 0.0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int yearSinceStart = pp.getYear() - projectionResult.getStartYear();
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.getRestorationCosts();
			}
		}
		return (int) NumericUtils.roundProgressive(costs);
	}

	private Integer getRestorationYear(ProjectionResult projectionResult, CodeBuildingPart buildingPart) {
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			for (ProjectionElement re : pp.getRestorationElements()) {
				if (re.getBuildingPart().getId().equals(buildingPart.getId())) {
					return pp.getYear();
				}
			}
		}
		return null;
	}

	private Integer getRestorationCosts(ProjectionResult projectionResult, CodeBuildingPart buildingPart) {
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			for (ProjectionElement re : pp.getRestorationElements()) {
				if (re.getBuildingPart().getId().equals(buildingPart.getId())) {
					return (int) NumericUtils.roundProgressive(re.getRestorationCosts());
				}
			}
		}
		return null;
	}

	private void addParameter(List<EvaluationParameter> list, String name, String value) {
		if (value != null) {
			list.add(EvaluationParameter.builder().name(name).value(value).build());
		}
	}

	private Color getConditionColor(Integer condition) {
		if (condition == null) {
			return null;
		} else if (condition < 50) {
			return PortfolioEvaluationServiceImpl.VERY_BAD_CONDITION;
		} else if (condition < 70) {
			return PortfolioEvaluationServiceImpl.BAD_CONDITION;
		} else if (condition < 85) {
			return PortfolioEvaluationServiceImpl.OK_CONDITION;
		}
		return PortfolioEvaluationServiceImpl.GOOD_CONDITION;

	}

}
