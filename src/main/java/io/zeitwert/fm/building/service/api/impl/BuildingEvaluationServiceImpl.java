package io.zeitwert.fm.building.service.api.impl;

import static io.zeitwert.fm.util.NumericUtils.roundProgressive;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.BuildingEvaluationService;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationParameter;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.building.service.api.dto.ProjectionElement;

@Service("buildingEvaluationService")
public class BuildingEvaluationServiceImpl implements BuildingEvaluationService {

	public static final String SOFT_RETURN = "\u000B";

	public static final Color VERY_BAD_CONDITION = new Color(229, 79, 41);
	public static final Color BAD_CONDITION = new Color(250, 167, 36);
	public static final Color OK_CONDITION = new Color(120, 192, 107);
	public static final Color GOOD_CONDITION = new Color(51, 135, 33);

	private final ProjectionService projectionService;

	public BuildingEvaluationServiceImpl(ProjectionService projectionService) {
		this.projectionService = projectionService;
	}

	@Override
	public BuildingEvaluationResult getEvaluation(ObjBuilding building) {

		requireThis(building.getCurrentRating() != null, "has current rating");

		Formatter fmt = Formatter.INSTANCE;
		String value = null;

		ProjectionResult projectionResult = this.projectionService.getProjection(Set.of(building),
				ProjectionService.DefaultDuration);

		List<EvaluationParameter> facts = new ArrayList<>();
		List<EvaluationParameter> onePageFacts = new ArrayList<>();

		if (building.getBuildingNr() != null) {
			value = building.getBuildingNr();
			this.addParameter(facts, "Gebäudenummer", value);
			this.addParameter(onePageFacts, "Gebäudenummer", value);
		}
		if (building.getCurrentRating().getPartCatalog() != null) {
			value = building.getCurrentRating().getPartCatalog().getName();
			this.addParameter(facts, "Gebäudekategorie", value);
			this.addParameter(onePageFacts, "Gebäudekategorie", value);
		}
		if (building.getBuildingYear() != null && building.getBuildingYear() > 0) {
			value = Integer.toString(building.getBuildingYear());
			this.addParameter(facts, "Baujahr", value);
			this.addParameter(onePageFacts, "Baujahr", value);
		}
		if (building.getInsuredValue() != null) {
			value = fmt.formatMonetaryValue(1000 * building.getInsuredValue().doubleValue(), "CHF");
			this.addParameter(facts, "GV-Neuwert (" + building.getInsuredValueYear() + ")", value);
			this.addParameter(onePageFacts, "GV-Neuwert (" + building.getInsuredValueYear() + ")", value);
		}
		if (building.getVolume() != null) {
			value = fmt.formatNumber(building.getVolume()) + " m³";
			this.addParameter(facts, "Volumen Rauminhalt SIA 416", value);
		}
		if (building.getCurrentRating().getRatingDate() != null) {
			value = fmt.formatDate(building.getCurrentRating().getRatingDate());
			this.addParameter(facts, "Begehung am", value);
		}

		String timeValue = fmt.formatMonetaryValue(projectionResult.getPeriodList().get(0).getTimeValue(), "CHF");
		String shortTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 0, 1), "CHF");
		String midTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 2, 5), "CHF");
		String longTermRestoration = fmt.formatMonetaryValue(this.getRestorationCosts(projectionResult, 6, 25), "CHF");
		String averageMaintenance = fmt.formatMonetaryValue(this.getAverageMaintenanceCosts(projectionResult, 1, 5), "CHF");

		Integer ratingYear = 9999;
		int elementCount = 0;
		int totalWeight = 0;
		int totalCondition = 0;
		List<EvaluationElement> elements = new ArrayList<>();
		for (ObjBuildingPartElementRating element : building.getCurrentRating().getElementList()) {
			if (element.getWeight() != null && element.getWeight() > 0) {
				String description = this.replaceEol(element.getDescription());
				if (!StringUtils.isEmpty(element.getConditionDescription())) {
					description += "<br/><b>Zustand</b>: " + element.getConditionDescription();
				}
				if (!StringUtils.isEmpty(element.getMeasureDescription())) {
					description += "<br/><b>Massnahmen</b>: " + element.getMeasureDescription();
				}
				EvaluationElement dto = EvaluationElement.builder()
						.name(element.getBuildingPart().getName())
						.description(description)
						.weight(element.getWeight())
						.condition(element.getCondition())
						.conditionColor(this.getConditionColor(element.getCondition()))
						.restorationYear(this.getRestorationYear(projectionResult, element.getBuildingPart()))
						.restorationCosts(this.getRestorationCosts(projectionResult, element.getBuildingPart()))
						.build();
				elements.add(dto);
				if (element.getRatingYear() < ratingYear) {
					ratingYear = element.getRatingYear();
				}
				elementCount += 1;
				totalWeight += element.getWeight();
				totalCondition += element.getCondition();
			}
		}

		totalCondition = (int) Math.round(totalCondition / elementCount);
		EvaluationElement dto = EvaluationElement.builder()
				.name("Total")
				.weight(totalWeight)
				.condition(totalCondition)
				.conditionColor(this.getConditionColor(totalCondition))
				.build();
		elements.add(dto);

		List<EvaluationParameter> params = new ArrayList<>();
		this.addParameter(params, "Laufzeit (Zeithorizont)", "25 Jahre");
		this.addParameter(params, "Teuerung", String.format("%.1f", building.getInflationRate()) + " %");
		this.addParameter(params, "Z/N Wert", "" + totalCondition);
		this.addParameter(params, "Zeitwert", "" + timeValue);
		this.addParameter(params, "IS Kosten kurzfristig (0 - 1 Jahre)", shortTermRestoration);
		this.addParameter(params, "IS Kosten mittelfristig (2 - 5 Jahre)", midTermRestoration);
		this.addParameter(params, "IS Kosten langfristig (6 - 25 Jahre)", longTermRestoration);
		this.addParameter(params, "Durchschnittliche IH Kosten (nächste 5 Jahre)", averageMaintenance);

		List<EvaluationParameter> onePageParams = new ArrayList<>();
		this.addParameter(onePageParams, "Laufzeit (Zeithorizont); Teuerung",
				"25 Jahre; " + String.format("%.1f", building.getInflationRate()) + " %");
		this.addParameter(onePageParams, "Zeitwert (Z/N Wert: " + totalCondition + ")", "" + timeValue);
		this.addParameter(onePageParams, "IS Kosten kurzfristig (0 - 1 Jahre)", shortTermRestoration);
		this.addParameter(onePageParams, "IS Kosten mittelfristig (2 - 5 Jahre)", midTermRestoration);
		this.addParameter(onePageParams, "IS Kosten langfristig (6 - 25 Jahre)", longTermRestoration);
		this.addParameter(onePageParams, "Durchschnittliche IH Kosten (nächste 5 Jahre)", averageMaintenance);

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
					.totalCosts(totalCosts)
					.aggrCosts(aggrCosts)
					.build();
			periods.add(eps);
			if (pp.getRestorationElements().size() > 1) {
				for (ProjectionElement re : pp.getRestorationElements()) {
					restorationElement = re.getBuildingPart().getName();
					EvaluationPeriod epd = EvaluationPeriod.builder()
							.restorationCosts((int) re.getRestorationCosts())
							.restorationElement(restorationElement)
							.build();
					periods.add(epd);
				}
			}
		}

		return BuildingEvaluationResult.builder()
				.id(building.getId())
				.name(building.getName())
				.description(this.replaceEol(building.getDescription()))
				.address(building.getStreet() + ", " + building.getZip() + " " + building.getCity() + ", "
						+ building.getCountry().getName())
				.accountName(building.getAccount().getName())
				.facts(facts)
				.params(params)
				.onePageFacts(onePageFacts)
				.onePageParams(onePageParams)
				.ratingYear(ratingYear)
				.elements(elements)
				.startYear(projectionResult.getStartYear())
				.periods(periods)
				.build();
	}

	private String replaceEol(String text) {
		return text != null ? text.replace("<br>", SOFT_RETURN) : "";
	}

	private int getAverageMaintenanceCosts(ProjectionResult projectionResult, int startYear, int endYear) {
		requireThis(startYear <= endYear, "valid years");
		double costs = 0.0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int yearSinceStart = pp.getYear() - projectionResult.getStartYear();
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.getMaintenanceCosts();
			}
		}
		return (int) roundProgressive(costs / (endYear - startYear + 1));
	}

	private int getRestorationCosts(ProjectionResult projectionResult, int startYear, int endYear) {
		requireThis(startYear <= endYear, "valid years");
		double costs = 0.0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int yearSinceStart = pp.getYear() - projectionResult.getStartYear();
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.getRestorationCosts();
			}
		}
		return (int) roundProgressive(costs);
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
					return (int) roundProgressive(re.getRestorationCosts());
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
			return VERY_BAD_CONDITION;
		} else if (condition < 70) {
			return BAD_CONDITION;
		} else if (condition < 85) {
			return OK_CONDITION;
		}
		return GOOD_CONDITION;

	}

}
