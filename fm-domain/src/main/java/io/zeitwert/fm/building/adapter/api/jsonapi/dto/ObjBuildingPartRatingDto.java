package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.oe.model.ObjUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjBuildingPartRatingDto extends ObjPartDtoBase<ObjBuilding, ObjBuildingPartRating> {

	private Integer seqNr;
	private EnumeratedDto partCatalog;
	private EnumeratedDto maintenanceStrategy;
	private EnumeratedDto ratingStatus;
	private LocalDate ratingDate;
	private EnumeratedDto ratingUser;
	private List<ObjBuildingPartElementRatingDto> elements;

	public static ObjBuildingPartRatingDto fromPart(ObjBuildingPartRating part, ObjUserDtoAdapter userDtoAdapter, int seqNr) {
		if (part == null) {
			return null;
		}
		ObjBuildingPartRatingDtoBuilder<?, ?> dtoBuilder = ObjBuildingPartRatingDto.builder();
		ObjPartDtoBase.fromPart(dtoBuilder, part);
		return dtoBuilder
				.seqNr(seqNr)
				.partCatalog(EnumeratedDto.of(part.getPartCatalog()))
				.maintenanceStrategy(EnumeratedDto.of(part.getMaintenanceStrategy()))
				.ratingStatus(EnumeratedDto.of(part.getRatingStatus()))
				.ratingDate(part.getRatingDate())
				.ratingUser(userDtoAdapter.asEnumerated(part.getRatingUser()))
				.elements(part.getElementList().stream()
						.map(ObjBuildingPartElementRatingDto::fromPart).toList())
				.build();
	}

	@Override
	public void toPart(ObjBuildingPartRating rating) {
		super.toPart(rating);
		rating.setPartCatalog(this.partCatalog == null ? null : CodeBuildingPartCatalog.getPartCatalog(this.partCatalog.getId()));
		rating.setMaintenanceStrategy(this.maintenanceStrategy == null ? null : CodeBuildingMaintenanceStrategy.Enumeration.getMaintenanceStrategy(this.maintenanceStrategy.getId()));
		rating.setRatingStatus(this.ratingStatus == null ? null : CodeBuildingRatingStatus.getRatingStatus(this.ratingStatus.getId()));
		rating.setRatingDate(this.ratingDate);
	}

	public void setRatingUser(ObjBuildingPartRating rating, ObjUser user) {
		rating.setRatingUser(user);
	}

}
