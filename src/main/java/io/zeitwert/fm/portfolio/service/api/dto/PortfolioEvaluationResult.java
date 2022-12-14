package io.zeitwert.fm.portfolio.service.api.dto;

import java.util.List;

import io.zeitwert.fm.building.service.api.dto.EvaluationBuilding;
import io.zeitwert.fm.building.service.api.dto.EvaluationElement;
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class PortfolioEvaluationResult {

	private Integer id;
	private String name;
	private String description;
	private String accountName;

	private List<EvaluationBuilding> buildings;

	private List<EvaluationElement> elements;

	private Integer startYear;
	private List<EvaluationPeriod> periods;

}
