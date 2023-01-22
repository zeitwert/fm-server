
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjPartDtoBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.part.model.base.PartStatus;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingPartElementRatingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatusEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoAdapter;

public final class ObjBuildingDtoAdapter extends FMObjDtoAdapter<ObjBuilding, ObjBuildingVRecord, ObjBuildingDto> {

	private static ObjBuildingDtoAdapter instance;

	private ObjBuildingDtoAdapter() {
	}

	public static final ObjBuildingDtoAdapter getInstance() {
		if (instance == null) {
			instance = new ObjBuildingDtoAdapter();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjBuildingDto dto, ObjBuilding obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			// @formatter:off
			obj.setAccountId(dto.getAccountId());
			obj.setName(dto.getName());
			obj.setDescription(dto.getDescription());
			obj.setBuildingNr(dto.getBuildingNr());
			obj.setInsuranceNr(dto.getInsuranceNr());
			obj.setPlotNr(dto.getPlotNr());
			obj.setNationalBuildingId(dto.getNationalBuildingId());
			obj.setHistoricPreservation(dto.getHistoricPreservation() == null ? null : CodeHistoricPreservationEnum.getHistoricPreservation(dto.getHistoricPreservation().getId()));

			obj.setBuildingType(dto.getBuildingType() == null ? null : CodeBuildingTypeEnum.getBuildingType(dto.getBuildingType().getId()));
			obj.setBuildingSubType(dto.getBuildingSubType() == null ? null : CodeBuildingSubTypeEnum.getBuildingSubType(dto.getBuildingSubType().getId()));
			obj.setBuildingYear(dto.getBuildingYear());
			obj.setStreet(dto.getStreet());
			obj.setZip(dto.getZip());
			obj.setCity(dto.getCity());
			obj.setCountry(dto.getCountry() == null ? null : CodeCountryEnum.getCountry(dto.getCountry().getId()));
			obj.setGeoAddress(dto.getGeoAddress());
			obj.setGeoCoordinates(dto.getGeoCoordinates());
			obj.setGeoZoom(dto.getGeoZoom());
			obj.setCurrency(dto.getCurrency() == null ? null : CodeCurrencyEnum.getCurrency(dto.getCurrency().getId()));
			obj.setVolume(dto.getVolume());
			obj.setAreaGross(dto.getAreaGross());
			obj.setAreaNet(dto.getAreaNet());
			obj.setNrOfFloorsAboveGround(dto.getNrOfFloorsAboveGround());
			obj.setNrOfFloorsBelowGround(dto.getNrOfFloorsBelowGround());
			obj.setInsuredValue(dto.getInsuredValue());
			obj.setInsuredValueYear(dto.getInsuredValueYear());
			obj.setNotInsuredValue(dto.getNotInsuredValue());
			obj.setNotInsuredValueYear(dto.getNotInsuredValueYear());
			obj.setThirdPartyValue(dto.getThirdPartyValue());
			obj.setThirdPartyValueYear(dto.getThirdPartyValueYear());

			if (dto.getContactIds() != null) {
				obj.clearContactSet();
				dto.getContactIds().forEach(id -> {
					obj.addContact(id);
				});
			}

			if (dto.getMeta() != null && dto.getMeta().hasOperation(ObjBuildingDto.AddRatingOperation)) {
				obj.addRating();
			} else if (dto.getRatingSeqNr() != null && dto.getRatingSeqNr() >= 0) {
				final ObjBuildingPartRating rating =
					obj.getCurrentRating() == null ||
					dto.getRatingSeqNr() > obj.getCurrentRating().getMeta().getSeqNr()
						? obj.addRating()
						: obj.getCurrentRating();
				rating.setPartCatalog(dto.getPartCatalog() == null ? null : CodeBuildingPartCatalogEnum.getPartCatalog(dto.getPartCatalog().getId()));
				rating.setMaintenanceStrategy(dto.getMaintenanceStrategy() == null ? null : CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy(dto.getMaintenanceStrategy().getId()));
				rating.setRatingStatus(dto.getRatingStatus() == null ? null : CodeBuildingRatingStatusEnum.getRatingStatus(dto.getRatingStatus().getId()));
				rating.setRatingDate(dto.getRatingDate());
				Integer userId = dto.getRatingUser() == null ? null : Integer.parseInt(dto.getRatingUser().getId());
				rating.setRatingUser(userId == null ? null : this.getUser(userId));
				if (dto.getElements() != null) {
					dto.getElements().forEach(elementDto -> {
						ObjBuildingPartElementRating element = null;
						if (elementDto.getPartId() == null) {
							assertThis(elementDto.getBuildingPart() != null, "valid dto buildingPart");
							CodeBuildingPart buildingPart = CodeBuildingPartEnum.getBuildingPart(elementDto.getBuildingPart().getId());
							assertThis(rating.getElement(buildingPart) != null, "valid rating buildingPart");
							element = rating.getElement(buildingPart);
						} else {
							element = rating.getElementById(elementDto.getPartId());
						}
						elementDto.toPart(element);
						if (element.getRatingYear() == null && rating.getRatingDate() != null) {
							element.setRatingYear(rating.getRatingDate().getYear());
						}
					});
				}
			}
			// @formatter:on

		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
		}
	}

	@Override
	public ObjBuildingDto fromAggregate(ObjBuilding obj) {
		if (obj == null) {
			return null;
		}
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		dtoBuilder
				.accountId(obj.getAccountId())
				.buildingType(EnumeratedDto.fromEnum(obj.getBuildingType()))
				.buildingSubType(EnumeratedDto.fromEnum(obj.getBuildingSubType()))
				.name(obj.getName())
				.description(obj.getDescription())
				.buildingNr(obj.getBuildingNr())
				.insuranceNr(obj.getInsuranceNr())
				.plotNr(obj.getPlotNr())
				.nationalBuildingId(obj.getNationalBuildingId())
				.historicPreservation(EnumeratedDto.fromEnum(obj.getHistoricPreservation()))
				.buildingYear(obj.getBuildingYear())
				.street(obj.getStreet())
				.zip(obj.getZip())
				.city(obj.getCity())
				.country(EnumeratedDto.fromEnum(obj.getCountry()))
				.geoAddress(obj.getGeoAddress())
				.geoCoordinates(obj.getGeoCoordinates())
				.geoZoom(obj.getGeoZoom())
				.coverFotoId(obj.getCoverFotoId())
				.currency(EnumeratedDto.fromEnum(obj.getCurrency()))
				.volume(obj.getVolume())
				.areaGross(obj.getAreaGross())
				.areaNet(obj.getAreaNet())
				.nrOfFloorsAboveGround(obj.getNrOfFloorsAboveGround())
				.nrOfFloorsBelowGround(obj.getNrOfFloorsBelowGround())
				.insuredValue(obj.getInsuredValue())
				.insuredValueYear(obj.getInsuredValueYear())
				.notInsuredValue(obj.getNotInsuredValue())
				.notInsuredValueYear(obj.getNotInsuredValueYear())
				.thirdPartyValue(obj.getThirdPartyValue())
				.thirdPartyValueYear(obj.getThirdPartyValueYear())
				.contactIds(obj.getContactSet());
		if (obj.getCurrentRating() != null) {
			ObjBuildingPartRating rating = obj.getCurrentRating();
			boolean isNew = ((PartSPI<?>) rating).getStatus() == PartStatus.CREATED;
			dtoBuilder
					.ratingId(isNew ? ObjPartDtoBase.ServerNewIdPrefix + rating.getId() : String.valueOf(rating.getId()))
					.ratingSeqNr((int) obj.getRatingList().stream().filter(r -> this.isActiveRating(r)).count() - 1)
					.partCatalog(EnumeratedDto.fromEnum(rating.getPartCatalog()))
					.maintenanceStrategy(EnumeratedDto.fromEnum(rating.getMaintenanceStrategy()))
					.ratingStatus(EnumeratedDto.fromEnum(rating.getRatingStatus()))
					.ratingDate(rating.getRatingDate())
					.ratingUser(userDtoAdapter.asEnumerated(rating.getRatingUser()))
					.elements(obj.getCurrentRating().getElementList().stream()
							.map(a -> ObjBuildingPartElementRatingDto.fromPart(a)).toList());
		}
		return dtoBuilder.build();
	}

	private boolean isActiveRating(ObjBuildingPartRating rating) {
		CodeBuildingRatingStatus RatingDiscarded = CodeBuildingRatingStatusEnum.getRatingStatus("discard");
		return rating.getRatingStatus() == null || rating.getRatingStatus() != RatingDiscarded;
	}

	@Override
	public ObjBuildingDto fromRecord(ObjBuildingVRecord obj) {
		if (obj == null) {
			return null;
		}
		EnumeratedDto ratingUser = obj.getRatingUserId() != null ? this.getUserEnumerated(obj.getRatingUserId()) : null;
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj);
		// @formatter:off
		dtoBuilder = dtoBuilder
			.accountId(obj.getAccountId())
			.buildingType(EnumeratedDto.fromEnum(CodeBuildingTypeEnum.getBuildingType(obj.getBuildingTypeId())))
			.buildingSubType(EnumeratedDto.fromEnum(CodeBuildingSubTypeEnum.getBuildingSubType(obj.getBuildingSubTypeId())))
			.name(obj.getName())
			.description(obj.getDescription())
			.buildingNr(obj.getBuildingNr())
			.insuranceNr(obj.getInsuranceNr())
			.plotNr(obj.getPlotNr())
			.nationalBuildingId(obj.getNationalBuildingId())
			.historicPreservation(EnumeratedDto.fromEnum(CodeHistoricPreservationEnum.getHistoricPreservation(obj.getHistoricPreservationId())))
			.buildingYear(obj.getBuildingYear())
			.street(obj.getStreet())
			.zip(obj.getZip())
			.city(obj.getCity())
			.country(EnumeratedDto.fromEnum(CodeCountryEnum.getCountry(obj.getCountryId())))
			.geoAddress(obj.getGeoAddress())
			.geoCoordinates(obj.getGeoCoordinates())
			.geoZoom(obj.getGeoZoom())
			.coverFotoId(obj.getCoverFotoId())
			.currency(EnumeratedDto.fromEnum(CodeCurrencyEnum.getCurrency(obj.getCurrencyId())))
			.volume(obj.getVolume())
			.areaGross(obj.getAreaGross())
			.areaNet(obj.getAreaNet())
			.nrOfFloorsAboveGround(obj.getNrOfFloorsAboveGround())
			.nrOfFloorsBelowGround(obj.getNrOfFloorsBelowGround())
			.insuredValue(obj.getInsuredValue())
			.insuredValueYear(obj.getInsuredValueYear())
			.notInsuredValue(obj.getNotInsuredValue())
			.notInsuredValueYear(obj.getNotInsuredValueYear())
			.thirdPartyValue(obj.getThirdPartyValue())
			.thirdPartyValueYear(obj.getThirdPartyValueYear())
			// .ratingId(isNew ? ObjPartDtoBase.ServerNewIdPrefix + rating.getId() : String.valueOf(rating.getId()))
			// .ratingSeqNr((int) obj.getRatingList().stream().filter(r -> this.isActiveRating(r)).count() - 1)
			.partCatalog(EnumeratedDto.fromEnum(CodeBuildingPartCatalogEnum.getPartCatalog(obj.getPartCatalogId())))
			.maintenanceStrategy(EnumeratedDto.fromEnum(CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy(obj.getMaintenanceStrategyId())))
			.ratingStatus(EnumeratedDto.fromEnum(CodeBuildingRatingStatusEnum.getRatingStatus(obj.getRatingStatusId())))
			.ratingDate(obj.getRatingDate())
			.ratingUser(ratingUser);
		// @formatter:on
		return dtoBuilder.build();
	}

}
