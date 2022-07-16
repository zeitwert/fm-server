
package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "building", resourcePath = "building/buildings", deletable = false)
public class ObjBuildingDto extends FMObjDtoBase<ObjBuilding> {

	public static final String AddRatingOperation = "addRating";

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			ObjAccount account = null;
			if (this.getOriginal() != null) {
				account = this.getOriginal().getAccount();
			} else if (this.accountId != null) {
				account = getRepository(ObjAccount.class).get(this.sessionInfo, this.accountId);
			}
			this.accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account, this.sessionInfo);
		}
		return this.accountDto;
	}

	// Crnk needs to see this to set mainContractId
	public void setAccount(ObjAccountDto account) {
	}

	private EnumeratedDto buildingType;
	private EnumeratedDto buildingSubType;
	private String name;
	private String description;
	private String buildingNr;
	private String insuranceNr;
	private String plotNr;
	private String nationalBuildingId;
	private EnumeratedDto historicPreservation;
	private Integer buildingYear;
	private String street;
	private String zip;
	private String city;
	private EnumeratedDto country;
	private String geoAddress;
	private String geoCoordinates;
	private Integer geoZoom;
	private EnumeratedDto currency;
	private BigDecimal volume;
	private BigDecimal areaGross;
	private BigDecimal areaNet;
	private Integer nrOfFloorsAboveGround;
	private Integer nrOfFloorsBelowGround;
	private BigDecimal insuredValue;
	private Integer insuredValueYear;
	private BigDecimal notInsuredValue;
	private Integer notInsuredValueYear;
	private BigDecimal thirdPartyValue;
	private Integer thirdPartyValueYear;

	private String ratingId;
	private Integer ratingSeqNr;
	private EnumeratedDto partCatalog;
	private EnumeratedDto maintenanceStrategy;
	private EnumeratedDto ratingStatus;
	private LocalDate ratingDate;
	private ObjUserDto ratingUser;
	private List<ObjBuildingPartElementRatingDto> elements;

	@JsonApiRelationId
	private Integer coverFotoId;

	@JsonIgnore
	private ObjDocumentDto coverFotoDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getCoverFoto() {
		if (this.coverFotoDto == null) {
			ObjDocument cf = null;
			if (this.getOriginal() != null) {
				cf = this.getOriginal().getCoverFoto();
			} else if (this.coverFotoId != null) {
				cf = getRepository(ObjDocument.class).get(this.sessionInfo, this.coverFotoId);
			}
			this.coverFotoDto = ObjDocumentDtoAdapter.getInstance().fromAggregate(cf, this.sessionInfo);
		}
		return this.coverFotoDto;
	}

	public void setCoverFoto(ObjDocumentDto coverFoto) {
	}

}
