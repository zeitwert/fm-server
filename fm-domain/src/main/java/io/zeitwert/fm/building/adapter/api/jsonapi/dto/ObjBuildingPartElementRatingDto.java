package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

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

	public static ObjBuildingPartElementRatingDto fromPart(ObjBuildingPartElementRating part) {
		if (part == null) {
			return null;
		}
		ObjBuildingPartElementRatingDtoBuilder<?, ?> dtoBuilder = ObjBuildingPartElementRatingDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		Integer restorationYear = null;
		Double restorationCosts = null;
		ObjBuilding building = part.getMeta().getAggregate();
		if (building.insuredValue != null) {
			if (part.weight != null && part.weight > 0
					&& part.condition != null && part.ratingYear != null) {
				ProjectionPeriod renovationPeriod = part.buildingPart.getNextRestoration(
						1000000.0,
						part.ratingYear,
						part.condition);
				restorationYear = renovationPeriod.getYear();
				double elementValue = part.weight / 100.0 * building.getBuildingValue(restorationYear) / 1000.0;
				restorationCosts = (double) Math.round(renovationPeriod.getRestorationCosts() / 1000000.0 * elementValue);
			}
		}
		// Set<EnumeratedDto> materialDescriptions =
		// part.getMaterialDescriptionSet().stream()
		// .map(a -> EnumeratedDto.of(a)).collect(Collectors.toSet());
		// Set<EnumeratedDto> conditionDescriptions =
		// part.getConditionDescriptionSet().stream()
		// .map(a -> EnumeratedDto.of(a)).collect(Collectors.toSet());
		// Set<EnumeratedDto> measureDescriptions =
		// part.getMeasureDescriptionSet().stream()
		// .map(a -> EnumeratedDto.of(a)).collect(Collectors.toSet());
		return dtoBuilder
				.buildingPart(EnumeratedDto.of(part.buildingPart))
				.weight(part.weight)
				.condition(part.condition)
				.ratingYear(part.ratingYear)
				.strain(part.strain)
				.strength(part.strength)
				.description(part.description)
				.conditionDescription(part.conditionDescription)
				.measureDescription(part.measureDescription)
				// .materialDescriptions(materialDescriptions)
				// .conditionDescriptions(conditionDescriptions)
				// .measureDescriptions(measureDescriptions)
				.restorationYear(restorationYear)
				.restorationCosts(restorationCosts)
				.lifeTime20(part.buildingPart.getLifetime(0.2))
				.lifeTime50(part.buildingPart.getLifetime(0.5))
				.lifeTime70(part.buildingPart.getLifetime(0.7))
				.lifeTime85(part.buildingPart.getLifetime(0.85))
				.lifeTime95(part.buildingPart.getLifetime(0.95))
				.lifeTime100(part.buildingPart.getLifetime(1.0))
				.build();
	}

	@Override
	public void toPart(ObjBuildingPartElementRating part) {
		super.toPart(part);
		part.buildingPart = this.buildingPart == null ? null : CodeBuildingPart.Enumeration.getBuildingPart(this.buildingPart.getId());
		part.weight = this.weight;
		part.condition = this.condition;
		part.ratingYear = this.ratingYear;
		part.strain = this.strain;
		part.strength = this.strength;
		part.description = this.description;
		part.conditionDescription = this.conditionDescription;
		part.measureDescription = this.measureDescription;
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

}
