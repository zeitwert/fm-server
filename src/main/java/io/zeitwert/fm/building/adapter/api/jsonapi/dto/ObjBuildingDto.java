package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.common.model.enums.CodeCurrencyEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
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
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.util.Assert;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "building", resourcePath = "building/buildings", deletable = false)
public class ObjBuildingDto extends FMObjDtoBase<ObjBuilding> {

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			ObjAccount hh = null;
			if (this.getOriginal() != null) {
				hh = this.getOriginal().getAccount();
			} else if (this.accountId != null) {
				hh = this.getRepository(ObjAccount.class).get(this.sessionInfo, this.accountId).get();
			}
			this.accountDto = ObjAccountDto.fromObj(hh, this.sessionInfo);
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
	}

	private EnumeratedDto buildingType;
	private EnumeratedDto buildingSubType;
	private EnumeratedDto buildingPartCatalog;
	private String name;
	private String description;
	private String buildingNr;
	private String buildingInsuranceNr;
	private String plotNr;
	private String nationalBuildingId;
	private EnumeratedDto historicPreservation;
	private Integer buildingYear;
	private String street;
	private String zip;
	private String city;
	private EnumeratedDto country;
	private EnumeratedDto currency;
	private BigDecimal volume;
	private BigDecimal areaGross;
	private BigDecimal areaNet;
	private Integer nrOfFloorsAboveGround;
	private Integer nrOfFloorsBelowGround;
	private EnumeratedDto buildingMaintenanceStrategy;
	private BigDecimal insuredValue;
	private Integer insuredValueYear;
	private BigDecimal notInsuredValue;
	private Integer notInsuredValueYear;
	private BigDecimal thirdPartyValue;
	private Integer thirdPartyValueYear;
	private List<ObjBuildingPartElementDto> elements;

	public void toObj(ObjBuilding obj) {
		super.toObj(obj);
		obj.setAccountId(this.accountId);

		obj.setName(this.name);
		obj.setDescription(this.description);
		obj.setBuildingNr(this.buildingNr);
		obj.setBuildingInsuranceNr(this.buildingInsuranceNr);
		obj.setPlotNr(this.plotNr);
		obj.setNationalBuildingId(this.nationalBuildingId);
		obj.setHistoricPreservation(this.historicPreservation == null ? null
				: CodeHistoricPreservationEnum.getHistoricPreservation(this.historicPreservation.getId()));

		obj.setBuildingType(
				this.buildingType == null ? null : CodeBuildingTypeEnum.getBuildingType(this.buildingType.getId()));
		obj.setBuildingSubType(
				this.buildingSubType == null ? null : CodeBuildingSubTypeEnum.getBuildingSubType(this.buildingSubType.getId()));
		obj.setBuildingPartCatalog(this.buildingPartCatalog == null ? null
				: CodeBuildingPartCatalogEnum.getBuildingPartCatalog(this.buildingPartCatalog.getId()));
		obj.setBuildingYear(this.buildingYear);
		obj.setStreet(this.street);
		obj.setZip(this.zip);
		obj.setCity(this.city);
		obj.setCountry(this.country == null ? null : CodeCountryEnum.getCountry(this.country.getId()));
		obj.setCurrency(this.currency == null ? null : CodeCurrencyEnum.getCurrency(this.currency.getId()));
		obj.setVolume(this.volume);
		obj.setAreaGross(this.areaGross);
		obj.setAreaNet(this.areaNet);
		obj.setNrOfFloorsAboveGround(this.nrOfFloorsAboveGround);
		obj.setNrOfFloorsBelowGround(this.nrOfFloorsBelowGround);
		obj.setBuildingMaintenanceStrategy(this.buildingMaintenanceStrategy == null ? null
				: CodeBuildingMaintenanceStrategyEnum.getBuildingMaintenanceStrategy(this.buildingMaintenanceStrategy.getId()));
		obj.setInsuredValue(this.insuredValue);
		obj.setInsuredValueYear(this.insuredValueYear);
		obj.setNotInsuredValue(this.notInsuredValue);
		obj.setNotInsuredValueYear(this.notInsuredValueYear);
		obj.setThirdPartyValue(this.thirdPartyValue);
		obj.setThirdPartyValueYear(this.thirdPartyValueYear);
		this.elements.forEach(elementDto -> {
			ObjBuildingPartElement element = null;
			if (elementDto.getId() == null) {
				Assert.isTrue(elementDto.getBuildingPart() != null, "valid buildingPart");
				element = obj.addElement(CodeBuildingPartEnum.getBuildingPart(elementDto.getBuildingPart().getId()));
			} else {
				element = obj.getElementById(elementDto.getId());
			}
			elementDto.toPart(element);
		});
	}

	public static ObjBuildingDto fromObj(ObjBuilding obj, SessionInfo sessionInfo, ProjectionService projectionService) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(obj);
		FMObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
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

	public static ObjBuildingDto fromRecord(ObjBuildingVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder().original(null);
		FMObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
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
