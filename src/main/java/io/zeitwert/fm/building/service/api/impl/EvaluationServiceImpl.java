package io.zeitwert.fm.building.service.api.impl;

import io.zeitwert.ddd.util.Formatter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.EvaluationService;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationParameter;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import io.zeitwert.fm.building.service.api.dto.PortfolioEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;
import io.zeitwert.fm.building.service.api.dto.RestorationElement;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("evaluationService")
public class EvaluationServiceImpl implements EvaluationService {

	public static final String SOFT_RETURN = "\u000B";

	public static final Color VERY_BAD_RATING = new Color(229, 79, 41);
	public static final Color BAD_RATING = new Color(250, 167, 36);
	public static final Color OK_RATING = new Color(120, 192, 107);
	public static final Color GOOD_RATING = new Color(51, 135, 33);

	private final ProjectionService projectionService;

	@Autowired
	public EvaluationServiceImpl(ProjectionService projectionService) {
		this.projectionService = projectionService;
	}

	public BuildingEvaluationResult getEvaluation(ObjBuilding building) {

		Formatter fmt = Formatter.INSTANCE;
		String value = null;

		List<EvaluationParameter> facts = new ArrayList<>();
		if (building.getBuildingPartCatalog() != null) {
			value = building.getBuildingPartCatalog().getName();
			this.addParameter(facts, "Gebäudekategorie", value);
		}
		if (building.getBuildingYear() != null) {
			value = Integer.toString(building.getBuildingYear());
			this.addParameter(facts, "Baujahr", value);
		}
		if (building.getInsuredValue() != null) {
			value = fmt.formatMonetaryValue(1000 * building.getInsuredValue().doubleValue(), "CHF");
			this.addParameter(facts, "GV-Neuwert (" + building.getInsuredValueYear() + ")", value);
		}
		if (building.getVolume() != null) {
			value = fmt.formatNumber(building.getVolume()) + " m³";
			this.addParameter(facts, "Volumen RI", value);
		}
		if (building.getAreaGross() != null && building.getAreaGross().longValue() > 0) {
			value = fmt.formatNumber(building.getAreaGross()) + " m²";
			this.addParameter(facts, "Fläche GF", value);
		}
		if (building.getBuildingType() != null) {
			value = building.getBuildingType().getName();
			this.addParameter(facts, "Gebäudeart", value);
		}
		if (building.getBuildingSubType() != null) {
			value = building.getBuildingSubType().getName();
			this.addParameter(facts, "Gebäudetyp", value);
		}

		List<EvaluationParameter> params = new ArrayList<>();
		this.addParameter(params, "Laufzeit", "25 Jahre");
		this.addParameter(params, "Teuerung", String.format("%.1f", ProjectionServiceImpl.DefaultInflationRate) + " %");

		ProjectionResult projectionResult = projectionService.getProjection(building);

		Integer ratingYear = 9999;
		List<EvaluationElement> elements = new ArrayList<>();
		for (ObjBuildingPartElement element : building.getElementList()) {
			if (element.getValuePart() > 0) {
				EvaluationElement dto = EvaluationElement.builder()
						.name(element.getBuildingPart().getName())
						.description(this.replaceEol(element.getDescription()))
						.valuePart(element.getValuePart())
						.rating(element.getCondition())
						.ratingColor(getRatingColor(element.getCondition()))
						.restorationYear(getRestorationYear(projectionResult, building, element.getBuildingPart()))
						.restorationCosts(getRestorationCosts(projectionResult, building, element.getBuildingPart()))
						.build();
				elements.add(dto);
				if (element.getConditionYear() < ratingYear) {
					ratingYear = element.getConditionYear();
				}
			}
		}

		List<EvaluationPeriod> periods = new ArrayList<>();
		int aggrCosts = 0;
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			int maintenanceCosts = 1000 * (int) Math.round(pp.getMaintenanceCosts() / 1000);
			int restorationCosts = 1000 * (int) Math.round(pp.getRestorationCosts() / 1000);
			int totalCosts = maintenanceCosts + restorationCosts;
			aggrCosts += totalCosts;
			String restorationElement = "";
			if (pp.getRestorationElements().size() == 1) {
				restorationElement = pp.getRestorationElements().get(0).getBuildingPart().getName();
			}
			EvaluationPeriod eps = EvaluationPeriod.builder()
					.year(pp.getYear())
					.originalValue(1000 * (int) Math.round(pp.getOriginalValue() / 1000))
					.timeValue(1000 * (int) Math.round(pp.getTimeValue() / 1000))
					.maintenanceCosts(maintenanceCosts)
					.restorationCosts(restorationCosts)
					.restorationElement(restorationElement)
					.totalCosts(totalCosts)
					.aggrCosts(aggrCosts)
					.build();
			periods.add(eps);
			if (pp.getRestorationElements().size() > 1) {
				for (RestorationElement re : pp.getRestorationElements()) {
					restorationCosts = 1000 * (int) Math.round(re.getRestorationCosts() / 1000);
					restorationElement = re.getBuildingPart().getName();
					EvaluationPeriod epd = EvaluationPeriod.builder()
							.restorationCosts(restorationCosts)
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
				.ratingYear(ratingYear)
				.elements(elements)
				.startYear(projectionResult.getStartYear())
				.periods(periods)
				.build();
	}

	private String replaceEol(String text) {
		return text != null ? text.replace("<br>", SOFT_RETURN) : "";
	}

	private Integer getRestorationYear(ProjectionResult projectionResult, ObjBuilding building,
			CodeBuildingPart buildingPart) {
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			for (RestorationElement re : pp.getRestorationElements()) {
				if (re.getBuildingPart().getId().equals(buildingPart.getId())) {
					return pp.getYear();
				}
			}
		}
		return null;
	}

	private Integer getRestorationCosts(ProjectionResult projectionResult, ObjBuilding building,
			CodeBuildingPart buildingPart) {
		for (ProjectionPeriod pp : projectionResult.getPeriodList()) {
			for (RestorationElement re : pp.getRestorationElements()) {
				if (re.getBuildingPart().getId().equals(buildingPart.getId())) {
					return 1000 * (int) Math.round(re.getRestorationCosts() / 1000.0);
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

	private Color getRatingColor(Integer rating) {
		if (rating < 50) {
			return VERY_BAD_RATING;
		} else if (rating < 70) {
			return BAD_RATING;
		} else if (rating < 85) {
			return OK_RATING;
		}
		return GOOD_RATING;

	}

	public PortfolioEvaluationResult getEvaluation(ObjPortfolio portfolio) {
		return null;
	}

}
