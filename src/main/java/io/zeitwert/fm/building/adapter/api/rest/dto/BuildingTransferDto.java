package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data()
@Builder
public class BuildingTransferDto {

	private TransferMetaDto meta;

	private Integer id;
	private String name;
	private String description;

	private String buildingNr;
	private String buildingInsuranceNr;
	private String plotNr;
	private String nationalBuildingId;

	private String historicPreservation;

	private String street;
	private String zip;
	private String city;
	private String country;

	private String geoAddress;
	private String geoCoordinates;
	private Integer geoZoom;

	private String currency;

	private BigDecimal volume;
	private BigDecimal areaGross;
	private BigDecimal areaNet;
	private Integer nrOfFloorsAboveGround;
	private Integer nrOfFloorsBelowGround;

	private String buildingType;
	private String buildingSubType;
	private Integer buildingYear;
	private BigDecimal insuredValue;
	private Integer insuredValueYear;
	private BigDecimal notInsuredValue;
	private Integer notInsuredValueYear;
	private BigDecimal thirdPartyValue;
	private Integer thirdPartyValueYear;

	private String buildingPartCatalog;
	private String buildingMaintenanceStrategy;

	private List<BuildingTransferElementDto> elements;

}
