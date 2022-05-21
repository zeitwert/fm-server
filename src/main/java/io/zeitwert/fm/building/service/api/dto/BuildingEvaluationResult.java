package io.zeitwert.fm.building.service.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data()
@Builder
public class BuildingEvaluationResult {

	private Integer id;
	private String name;
	private String description;
	private String address;

	private String accountName;

	private List<EvaluationParameter> facts;
	private List<EvaluationParameter> params;

	private Integer ratingYear;

	private List<EvaluationElement> elements;

	private Integer startYear;
	private List<EvaluationPeriod> periods;

}
