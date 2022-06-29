
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingPartElementRatingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatusEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoBridge;

public final class ObjBuildingDtoBridge extends FMObjDtoBridge<ObjBuilding, ObjBuildingVRecord, ObjBuildingDto> {

	private static ObjBuildingDtoBridge instance;

	private ObjBuildingDtoBridge() {
	}

	public static final ObjBuildingDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjBuildingDtoBridge();
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

			if (dto.getMeta() != null && dto.getMeta().hasOperation(ObjBuildingDto.AddRatingOperation)) {
				obj.addRating();
			} else if (obj.getCurrentRating() != null) {
				ObjBuildingPartRating rating = obj.getCurrentRating();
				rating.setPartCatalog(dto.getPartCatalog() == null ? null : CodeBuildingPartCatalogEnum.getPartCatalog(dto.getPartCatalog().getId()));
				rating.setMaintenanceStrategy(dto.getMaintenanceStrategy() == null ? null : CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy(dto.getMaintenanceStrategy().getId()));
				rating.setRatingStatus(dto.getRatingStatus() == null ? null : CodeBuildingRatingStatusEnum.getRatingStatus(dto.getRatingStatus().getId()));
				rating.setRatingDate(dto.getRatingDate());
				rating.setRatingUser(dto.getRatingUser() == null ? null : getUserRepository().get(dto.getRatingUser().getId()));
				dto.getElements().forEach(elementDto -> {
					ObjBuildingPartElementRating element = null;
					if (elementDto.getId() == null) {
						assertThis(elementDto.getBuildingPart() != null, "valid buildingPart");
						element = rating.addElement(CodeBuildingPartEnum.getBuildingPart(elementDto.getBuildingPart().getId()));
					} else {
						element = rating.getElementById(elementDto.getId());
					}
					elementDto.toPart(element);
				});
			}
			// @formatter:on

		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
		}
	}

	@Override
	public ObjBuildingDto fromAggregate(ObjBuilding obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjUserDtoBridge userBridge = ObjUserDtoBridge.getInstance();
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj, sessionInfo);
		ProjectionService projectionService = AppContext.getInstance().getBean(ProjectionService.class);
		// @formatter:off
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
			.thirdPartyValueYear(obj.getThirdPartyValueYear());
		if (obj.getCurrentRating() != null) {
			ObjBuildingPartRating rating = obj.getCurrentRating();
			dtoBuilder
				.ratingId(rating.getId())
				.ratingSeqNr((int) obj.getRatingList().stream().filter(r -> this.isActiveRating(r)).count())
				.partCatalog(EnumeratedDto.fromEnum(rating.getPartCatalog()))
				.maintenanceStrategy(EnumeratedDto.fromEnum(rating.getMaintenanceStrategy()))
				.ratingStatus(EnumeratedDto.fromEnum(rating.getRatingStatus()))
				.ratingDate(rating.getRatingDate())
				.ratingUser(userBridge.fromAggregate(rating.getRatingUser(), sessionInfo))
				.elements(obj.getCurrentRating().getElementList().stream().map(a -> ObjBuildingPartElementRatingDto.fromPart(a, projectionService)).toList());
		}
		// @formatter:on
		return dtoBuilder.build();
	}

	private boolean isActiveRating(ObjBuildingPartRating rating) {
		CodeBuildingRatingStatus RatingDiscarded = CodeBuildingRatingStatusEnum.getRatingStatus("discard");
		return rating.getRatingStatus() == null || rating.getRatingStatus() != RatingDiscarded;
	}

	@Override
	public ObjBuildingDto fromRecord(ObjBuildingVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj, sessionInfo);
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
			.thirdPartyValueYear(obj.getThirdPartyValueYear());
		// if (obj.getCurrentRating() != null) {
		// .partCatalog(EnumeratedDto.fromEnum(CodeBuildingPartCatalogEnum.getPartCatalog(obj.getPartCatalogId())))
		// .buildingMaintenanceStrategy(EnumeratedDto.fromEnum(CodeBuildingMaintenanceStrategyEnum.getBuildingMaintenanceStrategy(obj.getBuildingMaintenanceStrategyId())))
		//.elements(obj.getElementList().stream().map(a -> ObjBuildingPartElementDto.fromPart(a)).toList())
		// }
		// @formatter:on
		return dtoBuilder.build();
	}

}
