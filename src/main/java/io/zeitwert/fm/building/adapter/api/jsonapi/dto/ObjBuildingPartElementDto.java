package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescriptionEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.service.api.ProjectionPeriod;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjBuildingPartElementDto extends ObjPartDtoBase<ObjBuilding, ObjBuildingPartElement> {

	private EnumeratedDto buildingPart;
	private Integer valuePart;
	private Integer condition;
	private Integer conditionYear;
	private Integer strain;
	private Integer strength;
	private String description;

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

	public void toPart(ObjBuildingPartElement part) {
		super.toPart(part);
		part.setBuildingPart(
				this.buildingPart == null ? null : CodeBuildingPartEnum.getBuildingPart(this.buildingPart.getId()));
		part.setValuePart(this.valuePart);
		part.setCondition(this.condition);
		part.setConditionYear(this.conditionYear);
		part.setStrain(this.strain);
		part.setStrength(this.strength);
		part.setDescription(this.description);
		if (this.materialDescriptions != null) {
			part.clearMaterialDescriptionSet();
			this.materialDescriptions.forEach(description -> part.addMaterialDescription(
					CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		}
		if (this.conditionDescriptions != null) {
			part.clearConditionDescriptionSet();
			this.conditionDescriptions.forEach(description -> part.addConditionDescription(
					CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		}
		if (this.measureDescriptions != null) {
			part.clearMeasureDescriptionSet();
			this.measureDescriptions.forEach(description -> part.addMeasureDescription(
					CodeBuildingElementDescriptionEnum.getBuildingElementDescription(description.getId())));
		}
	}

	public static ObjBuildingPartElementDto fromPart(ObjBuildingPartElement part, ProjectionService projectionService) {
		if (part == null) {
			return null;
		}
		ObjBuildingPartElementDtoBuilder<?, ?> dtoBuilder = ObjBuildingPartElementDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		Integer restorationYear = null;
		Double restorationCosts = null;
		ObjBuilding building = part.getMeta().getAggregate();
		if (building.getInsuredValue() != null) {
			if (part.getValuePart() > 0 && part.getCondition() != null && part.getConditionYear() != null) {
				//@formatter:off

				ProjectionPeriod renovationPeriod =
					projectionService.getNextRestoration(
						part.getBuildingPart(),
						1000000.0,
						part.getConditionYear(),
						part.getCondition()
					);
				//@formatter:on
				restorationYear = renovationPeriod.getYear();
				double elementValue = part.getValuePart() / 100.0 * building.getBuildingValue(restorationYear) / 1000.0;
				restorationCosts = (double) Math.round(renovationPeriod.getRestorationCosts() / 1000000.0 * elementValue);
			}
		}
		// @formatter:off
		return dtoBuilder
			.buildingPart(EnumeratedDto.fromEnum(part.getBuildingPart()))
			.valuePart(part.getValuePart())
			.condition(part.getCondition())
			.conditionYear(part.getConditionYear())
			.strain(part.getStrain())
			.strength(part.getStrength())
			.description(part.getDescription())
			.materialDescriptions(part.getMaterialDescriptionSet().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.conditionDescriptions(part.getConditionDescriptionSet().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.measureDescriptions(part.getMeasureDescriptionSet().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.restorationYear(restorationYear)
			.restorationCosts(restorationCosts)
			.lifeTime20(projectionService.getLifetime(part.getBuildingPart(), 0.2))
			.lifeTime50(projectionService.getLifetime(part.getBuildingPart(), 0.5))
			.lifeTime70(projectionService.getLifetime(part.getBuildingPart(), 0.7))
			.lifeTime85(projectionService.getLifetime(part.getBuildingPart(), 0.85))
			.lifeTime95(projectionService.getLifetime(part.getBuildingPart(), 0.95))
			.lifeTime100(projectionService.getLifetime(part.getBuildingPart(), 1.0))
			.build();
		// @formatter:on
	}

}
