
package io.zeitwert.fm.building.adapter.api.jsonapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.impl.ObjBuildingDtoAdapter;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
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
@JsonApiResource(type = "building", resourcePath = "building/buildings")
public class ObjBuildingDto extends ObjDtoBase<ObjBuilding> {

	public static final String AddRatingOperation = "addRating";

	@Override
	public ObjBuildingDtoAdapter getAdapter() {
		return (ObjBuildingDtoAdapter) super.getAdapter();
	}

	@JsonApiRelationId
	private Integer accountId;

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
		this.accountDto = null;
	}

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			this.accountDto = this.getAdapter().getAccountDto(this.accountId);
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
		this.accountDto = account;
		this.accountId = account != null ? account.getId() : null;
	}

	@JsonApiRelationId
	private Set<Integer> contactIds;

	public void setContactIds(Set<Integer> contactIds) {
		this.contactIds = contactIds;
		this.contactsDtos = null;
	}

	@JsonIgnore
	private Set<ObjContactDto> contactsDtos;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public Set<ObjContactDto> getContacts() {
		if (this.contactsDtos == null) {
			this.contactsDtos = this.contactIds != null
					? this.contactIds
							.stream()
							.map(id -> this.getAdapter().getContactDto(id))
							.collect(Collectors.toSet())
					: Set.of();
		}
		return this.contactsDtos;
	}

	public void setContacts(Set<ObjContactDto> contacts) {
		this.contactsDtos = contacts;
		this.contactIds = contacts != null
				? contacts.stream().map(ct -> ct.getId()).collect(Collectors.toSet())
				: null;
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
	private EnumeratedDto ratingUser;
	private List<ObjBuildingPartElementRatingDto> elements;

	@JsonApiRelationId
	private Integer coverFotoId;

	@JsonIgnore
	private ObjDocumentDto coverFotoDto;

	public void setCoverFotoId(Integer fotoId) {
		// assertThis(false, "coverFotoId is read-only");
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getCoverFoto() {
		if (this.coverFotoDto == null) {
			this.coverFotoDto = this.getAdapter().getDocumentDto(this.coverFotoId);
		}
		return this.coverFotoDto;
	}

	public void setCoverFoto(ObjDocumentDto coverFoto) {
		// assertThis(false, "coverFoto is read-only");
	}

}
