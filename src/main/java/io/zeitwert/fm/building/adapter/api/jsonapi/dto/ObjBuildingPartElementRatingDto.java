package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjBuildingPartElementRatingDto extends ObjPartDtoBase<ObjBuilding, ObjBuildingPartElementRating> {

	private EnumeratedDto buildingPart;
	private Integer weight;
	private Integer condition;
	private Integer ratingYear;
	private Integer strain;
	private Integer strength;
	private String description;
	private String conditionDescription;
	private String measureDescription;

	private Integer restorationYear;
	private Double restorationCosts;

	private Integer lifeTime20;
	private Integer lifeTime50;
	private Integer lifeTime70;
	private Integer lifeTime85;
	private Integer lifeTime95;
	private Integer lifeTime100;

	private Set<EnumeratedDto> materialDescriptions;
	private Set<EnumeratedDto> conditionDescriptions;
	private Set<EnumeratedDto> measureDescriptions;

	@Override
	public void toPart(ObjBuildingPartElementRating part) {
		super.toPart(part);
		part.setBuildingPart(
				this.buildingPart == null ? null : CodeBuildingPartEnum.getBuildingPart(this.buildingPart.getId()));
		part.setWeight(this.weight);
		part.setCondition(this.condition);
		part.setRatingYear(this.ratingYear);
		part.setStrain(this.strain);
		part.setStrength(this.strength);
		part.setDescription(this.description);
		part.setConditionDescription(this.conditionDescription);
		part.setMeasureDescription(this.measureDescription);
		// if (this.materialDescriptions != null) {
		// part.clearMaterialDescriptionSet();
		// this.materialDescriptions.forEach(description -> part.addMaterialDescription(
		// CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		// }
		// if (this.conditionDescriptions != null) {
		// part.clearConditionDescriptionSet();
		// this.conditionDescriptions.forEach(description ->
		// part.addConditionDescription(
		// CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		// }
		// if (this.measureDescriptions != null) {
		// part.clearMeasureDescriptionSet();
		// this.measureDescriptions.forEach(description -> part.addMeasureDescription(
		// CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		// }
	}

	public static ObjBuildingPartElementRatingDto fromPart(ObjBuildingPartElementRating part) {
		if (part == null) {
			return null;
		}
		ObjBuildingPartElementRatingDtoBuilder<?, ?> dtoBuilder = ObjBuildingPartElementRatingDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		Integer restorationYear = null;
		Double restorationCosts = null;
		ObjBuilding building = part.getMeta().getAggregate();
		if (building.getInsuredValue() != null) {
			if (part.getWeight() != null && part.getWeight() > 0
					&& part.getCondition() != null && part.getRatingYear() != null) {
				ProjectionPeriod renovationPeriod = part.getBuildingPart().getNextRestoration(
						1000000.0,
						part.getRatingYear(),
						part.getCondition());
				restorationYear = renovationPeriod.getYear();
				double elementValue = part.getWeight() / 100.0 * building.getBuildingValue(restorationYear) / 1000.0;
				restorationCosts = (double) Math.round(renovationPeriod.getRestorationCosts() / 1000000.0 * elementValue);
			}
		}
		// Set<EnumeratedDto> materialDescriptions =
		// part.getMaterialDescriptionSet().stream()
		// .map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet());
		// Set<EnumeratedDto> conditionDescriptions =
		// part.getConditionDescriptionSet().stream()
		// .map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet());
		// Set<EnumeratedDto> measureDescriptions =
		// part.getMeasureDescriptionSet().stream()
		// .map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet());
		return dtoBuilder
				.buildingPart(EnumeratedDto.fromEnum(part.getBuildingPart()))
				.weight(part.getWeight())
				.condition(part.getCondition())
				.ratingYear(part.getRatingYear())
				.strain(part.getStrain())
				.strength(part.getStrength())
				.description(part.getDescription())
				.conditionDescription(part.getConditionDescription())
				.measureDescription(part.getMeasureDescription())
				// .materialDescriptions(materialDescriptions)
				// .conditionDescriptions(conditionDescriptions)
				// .measureDescriptions(measureDescriptions)
				.restorationYear(restorationYear)
				.restorationCosts(restorationCosts)
				.lifeTime20(part.getBuildingPart().getLifetime(0.2))
				.lifeTime50(part.getBuildingPart().getLifetime(0.5))
				.lifeTime70(part.getBuildingPart().getLifetime(0.7))
				.lifeTime85(part.getBuildingPart().getLifetime(0.85))
				.lifeTime95(part.getBuildingPart().getLifetime(0.95))
				.lifeTime100(part.getBuildingPart().getLifetime(1.0))
				.build();
	}

}
