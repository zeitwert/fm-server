
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.common.model.enums.CodeCurrencyEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingPartElementDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoBridge;

import static io.zeitwert.ddd.util.Check.assertThis;

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
		super.toAggregate(dto, obj);
		obj.setAccountId(dto.getAccountId());

		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setBuildingNr(dto.getBuildingNr());
		obj.setBuildingInsuranceNr(dto.getBuildingInsuranceNr());
		obj.setPlotNr(dto.getPlotNr());
		obj.setNationalBuildingId(dto.getNationalBuildingId());
		obj.setHistoricPreservation(dto.getHistoricPreservation() == null ? null
				: CodeHistoricPreservationEnum.getHistoricPreservation(dto.getHistoricPreservation().getId()));

		obj.setBuildingType(
				dto.getBuildingType() == null ? null : CodeBuildingTypeEnum.getBuildingType(dto.getBuildingType().getId()));
		obj.setBuildingSubType(
				dto.getBuildingSubType() == null ? null
						: CodeBuildingSubTypeEnum.getBuildingSubType(dto.getBuildingSubType().getId()));
		obj.setBuildingPartCatalog(dto.getBuildingPartCatalog() == null ? null
				: CodeBuildingPartCatalogEnum.getBuildingPartCatalog(dto.getBuildingPartCatalog().getId()));
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
		obj.setBuildingMaintenanceStrategy(dto.getBuildingMaintenanceStrategy() == null ? null
				: CodeBuildingMaintenanceStrategyEnum
						.getBuildingMaintenanceStrategy(dto.getBuildingMaintenanceStrategy().getId()));
		obj.setInsuredValue(dto.getInsuredValue());
		obj.setInsuredValueYear(dto.getInsuredValueYear());
		obj.setNotInsuredValue(dto.getNotInsuredValue());
		obj.setNotInsuredValueYear(dto.getNotInsuredValueYear());
		obj.setThirdPartyValue(dto.getThirdPartyValue());
		obj.setThirdPartyValueYear(dto.getThirdPartyValueYear());
		dto.getElements().forEach(elementDto -> {
			ObjBuildingPartElement element = null;
			if (elementDto.getId() == null) {
				assertThis(elementDto.getBuildingPart() != null, "valid buildingPart");
				element = obj.addElement(CodeBuildingPartEnum.getBuildingPart(elementDto.getBuildingPart().getId()));
			} else {
				element = obj.getElementById(elementDto.getId());
			}
			elementDto.toPart(element);
		});
	}

	@Override
	public ObjBuildingDto fromAggregate(ObjBuilding obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj, sessionInfo);
		ProjectionService projectionService = AppContext.getInstance().getBean(ProjectionService.class);
		// @formatter:off
		return dtoBuilder
			.accountId(obj.getAccountId())
			.buildingType(EnumeratedDto.fromEnum(obj.getBuildingType()))
			.buildingSubType(EnumeratedDto.fromEnum(obj.getBuildingSubType()))
			.buildingPartCatalog(EnumeratedDto.fromEnum(obj.getBuildingPartCatalog()))
			.name(obj.getName())
			.description(obj.getDescription())
			.buildingNr(obj.getBuildingNr())
			.buildingInsuranceNr(obj.getBuildingInsuranceNr())
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
			.buildingMaintenanceStrategy(EnumeratedDto.fromEnum(obj.getBuildingMaintenanceStrategy()))
			.insuredValue(obj.getInsuredValue())
			.insuredValueYear(obj.getInsuredValueYear())
			.notInsuredValue(obj.getNotInsuredValue())
			.notInsuredValueYear(obj.getNotInsuredValueYear())
			.thirdPartyValue(obj.getThirdPartyValue())
			.thirdPartyValueYear(obj.getThirdPartyValueYear())
			.elements(obj.getElementList().stream().map(a -> ObjBuildingPartElementDto.fromPart(a, projectionService)).toList())
			.build();
		// @formatter:on
	}

	@Override
	public ObjBuildingDto fromRecord(ObjBuildingVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.accountId(obj.getAccountId())
			.buildingType(EnumeratedDto.fromEnum(CodeBuildingTypeEnum.getBuildingType(obj.getBuildingTypeId())))
			.buildingSubType(EnumeratedDto.fromEnum(CodeBuildingSubTypeEnum.getBuildingSubType(obj.getBuildingSubTypeId())))
			.buildingPartCatalog(EnumeratedDto.fromEnum(CodeBuildingPartCatalogEnum.getBuildingPartCatalog(obj.getBuildingPartCatalogId())))
			.name(obj.getName())
			.description(obj.getDescription())
			.buildingNr(obj.getBuildingNr())
			.buildingInsuranceNr(obj.getBuildingInsuranceNr())
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
			.buildingMaintenanceStrategy(EnumeratedDto.fromEnum(CodeBuildingMaintenanceStrategyEnum.getBuildingMaintenanceStrategy(obj.getBuildingMaintenanceStrategyId())))
			.insuredValue(obj.getInsuredValue())
			.insuredValueYear(obj.getInsuredValueYear())
			.notInsuredValue(obj.getNotInsuredValue())
			.notInsuredValueYear(obj.getNotInsuredValueYear())
			.thirdPartyValue(obj.getThirdPartyValue())
			.thirdPartyValueYear(obj.getThirdPartyValueYear())
			//.elements(obj.getElementList().stream().map(a -> ObjBuildingPartElementDto.fromPart(a)).toList())
			.build();
		// @formatter:on
	}

}
