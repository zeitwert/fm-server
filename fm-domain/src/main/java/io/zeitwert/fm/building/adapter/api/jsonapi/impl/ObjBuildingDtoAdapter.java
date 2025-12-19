package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingPartElementRatingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.enums.*;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("objBuildingDtoAdapter")
public class ObjBuildingDtoAdapter extends ObjDtoAdapterBase<ObjBuilding, ObjBuildingDto> {

	private final ObjUserDtoAdapter userDtoAdapter;
	private RequestContextFM requestContext;
	private ObjAccountRepository accountRepository = null;
	private ObjAccountDtoAdapter accountDtoAdapter;
	private ObjContactRepository contactRepository = null;
	private ObjContactDtoAdapter contactDtoAdapter;
	private ObjDocumentRepository documentRepository = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	protected ObjBuildingDtoAdapter(ObjUserDtoAdapter userDtoAdapter) {
		this.userDtoAdapter = userDtoAdapter;
	}

	@Autowired
	public void setRequestContext(RequestContextFM requestContext) {
		this.requestContext = requestContext;
	}

	@Autowired
	void setAccountRepository(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	@Autowired
	void setContactRepository(ObjContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

	@Autowired
	void setContactDtoAdapter(ObjContactDtoAdapter contactDtoAdapter) {
		this.contactDtoAdapter = contactDtoAdapter;
	}

	@Autowired
	void setDocumentRepository(ObjDocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@Autowired
	void setDocumentDtoAdapter(ObjDocumentDtoAdapter documentDtoAdapter) {
		this.documentDtoAdapter = documentDtoAdapter;
	}

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(id)) : null;
	}

	public ObjContactDto getContactDto(Integer id) {
		return id != null ? this.contactDtoAdapter.fromAggregate(this.contactRepository.get(id)) : null;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjBuildingDto dto, ObjBuilding obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			// @formatter:off
			obj.accountId = dto.getAccountId();
			obj.name = dto.getName();
			obj.description = dto.getDescription();
			obj.buildingNr = dto.getBuildingNr();
			obj.insuranceNr = dto.getInsuranceNr();
			obj.plotNr = dto.getPlotNr();
			obj.nationalBuildingId = dto.getNationalBuildingId();
			obj.historicPreservation = dto.getHistoricPreservation() == null ? null : CodeHistoricPreservation.getHistoricPreservation(dto.getHistoricPreservation().getId());

			obj.buildingType = dto.getBuildingType() == null ? null : CodeBuildingType.getBuildingType(dto.getBuildingType().getId());
			obj.buildingSubType = dto.getBuildingSubType() == null ? null : CodeBuildingSubType.getBuildingSubType(dto.getBuildingSubType().getId());
			obj.buildingYear = dto.getBuildingYear();
			obj.street = dto.getStreet();
			obj.zip = dto.getZip();
			obj.city = dto.getCity();
			obj.country = dto.getCountry() == null ? null : CodeCountry.getCountry(dto.getCountry().getId());
			obj.geoAddress = dto.getGeoAddress();
			obj.geoCoordinates = dto.getGeoCoordinates();
			obj.geoZoom = dto.getGeoZoom();
			obj.currency = dto.getCurrency() == null ? null : CodeCurrency.getCurrency(dto.getCurrency().getId());
			obj.volume = dto.getVolume();
			obj.areaGross = dto.getAreaGross();
			obj.areaNet = dto.getAreaNet();
			obj.nrOfFloorsAboveGround = dto.getNrOfFloorsAboveGround();
			obj.nrOfFloorsBelowGround = dto.getNrOfFloorsBelowGround();
			obj.insuredValue = dto.getInsuredValue();
			obj.insuredValueYear = dto.getInsuredValueYear();
			obj.notInsuredValue = dto.getNotInsuredValue();
			obj.notInsuredValueYear = dto.getNotInsuredValueYear();
			obj.thirdPartyValue = dto.getThirdPartyValue();
			obj.thirdPartyValueYear = dto.getThirdPartyValueYear();

			if (dto.getContactIds() != null) {
				obj.clearContactSet();
				dto.getContactIds().forEach(obj::addContact);
			}

			if (dto.getMeta() != null && dto.getMeta().hasOperation(ObjBuildingDto.AddRatingOperation)) {
				obj.addRating((ObjUserFM) requestContext.getUser(), requestContext.getCurrentTime());
			} else if (dto.getRatingSeqNr() != null && dto.getRatingSeqNr() >= 0) {
				final ObjBuildingPartRating rating =
					obj.currentRating == null ||
					dto.getRatingSeqNr() >= obj.ratingList.size()
						? obj.addRating((ObjUserFM) requestContext.getUser(), requestContext.getCurrentTime())
						: obj.currentRating;
				rating.partCatalog = dto.getPartCatalog() == null ? null : CodeBuildingPartCatalog.getPartCatalog(dto.getPartCatalog().getId());
				rating.maintenanceStrategy = dto.getMaintenanceStrategy() == null ? null : CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(dto.getMaintenanceStrategy().getId());
				rating.ratingStatus = dto.getRatingStatus() == null ? null : CodeBuildingRatingStatus.getRatingStatus(dto.getRatingStatus().getId());
				rating.ratingDate = dto.getRatingDate();
				Integer userId = dto.getRatingUser() == null ? null : Integer.parseInt(dto.getRatingUser().getId());
				rating.ratingUser = userId == null ? null : this.getUser(userId);
				if (dto.getElements() != null) {
					dto.getElements().forEach(elementDto -> {
						ObjBuildingPartElementRating element = null;
						if (elementDto.getPartId() == null) {
//							TODO assertThis(elementDto.getBuildingPart() != null, "valid dto buildingPart");
							CodeBuildingPart buildingPart = CodeBuildingPart.Enumeration.getBuildingPart(elementDto.getBuildingPart().getId());
//							TODO assertThis(rating.getElement(buildingPart) != null, "valid rating buildingPart");
							element = rating.getElement(buildingPart);
						} else {
							element = rating.getElementById(elementDto.getPartId());
						}
						elementDto.toPart(element);
						if (element.ratingYear == null && rating.ratingDate != null) {
							element.ratingYear = rating.ratingDate.getYear();
						}
					});
				}
			}
			// @formatter:on

		} finally {
			obj.getMeta().enableCalc();
			obj.getMeta().calcAll();
		}
	}

	@Override
	public ObjBuildingDto fromAggregate(ObjBuilding obj) {
		if (obj == null) {
			return null;
		}
		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		dtoBuilder
				.accountId((Integer) obj.accountId)
				.buildingType(EnumeratedDto.of(obj.buildingType))
				.buildingSubType(EnumeratedDto.of(obj.buildingSubType))
				.name(obj.name)
				.description(obj.description)
				.buildingNr(obj.buildingNr)
				.insuranceNr(obj.insuranceNr)
				.plotNr(obj.plotNr)
				.nationalBuildingId(obj.nationalBuildingId)
				.historicPreservation(EnumeratedDto.of(obj.historicPreservation))
				.buildingYear(obj.buildingYear)
				.street(obj.street)
				.zip(obj.zip)
				.city(obj.city)
				.country(EnumeratedDto.of(obj.country))
				.geoAddress(obj.geoAddress)
				.geoCoordinates(obj.geoCoordinates)
				.geoZoom(obj.geoZoom)
				.coverFotoId(obj.coverFotoId)
				.currency(EnumeratedDto.of(obj.currency))
				.volume(obj.volume)
				.areaGross(obj.areaGross)
				.areaNet(obj.areaNet)
				.nrOfFloorsAboveGround(obj.nrOfFloorsAboveGround)
				.nrOfFloorsBelowGround(obj.nrOfFloorsBelowGround)
				.insuredValue(obj.insuredValue)
				.insuredValueYear(obj.insuredValueYear)
				.notInsuredValue(obj.notInsuredValue)
				.notInsuredValueYear(obj.notInsuredValueYear)
				.thirdPartyValue(obj.thirdPartyValue)
				.thirdPartyValueYear(obj.thirdPartyValueYear)
				.contactIds(obj.contactSet);
		if (obj.currentRating != null) {
			ObjBuildingPartRating rating = obj.currentRating;
//			boolean isNew = ((PartSPI<?>) rating).getPersistenceStatus() == PartPersistenceStatus.CREATED;
			dtoBuilder
//					.ratingId(isNew ? ObjPartDtoBase.ServerNewIdPrefix + rating.getId() : String.valueOf(rating.getId()))
					.ratingId(String.valueOf(rating.getId()))
					.ratingSeqNr((int) obj.ratingList.stream().filter(this::isActiveRating).count() - 1)
					.partCatalog(EnumeratedDto.of(rating.partCatalog))
					.maintenanceStrategy(EnumeratedDto.of(rating.maintenanceStrategy))
					.ratingStatus(EnumeratedDto.of(rating.ratingStatus))
					.ratingDate(rating.ratingDate)
					.ratingUser(userDtoAdapter.asEnumerated(rating.ratingUser))
					.elements(obj.currentRating.getElementList().stream()
							.map(ObjBuildingPartElementRatingDto::fromPart).toList());
		}
		return dtoBuilder.build();
	}

	private boolean isActiveRating(ObjBuildingPartRating rating) {
		return rating.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD;
	}

//	@Override
//	public ObjBuildingDto fromRecord(ObjBuildingVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		EnumeratedDto ratingUser = obj.getRatingUserId() != null ? this.getUserEnumerated(obj.getRatingUserId()) : null;
//		ObjBuildingDto.ObjBuildingDtoBuilder<?, ?> dtoBuilder = ObjBuildingDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		// @formatter:off
//		dtoBuilder = dtoBuilder
//			.accountId(obj.getAccountId())
//			.buildingType(EnumeratedDto.of(CodeBuildingType.getBuildingType(obj.getBuildingTypeId())))
//			.buildingSubType(EnumeratedDto.of(CodeBuildingSubType.getBuildingSubType(obj.getBuildingSubTypeId())))
//			.name(obj.getName())
//			.description(obj.getDescription())
//			.buildingNr(obj.getBuildingNr())
//			.insuranceNr(obj.getInsuranceNr())
//			.plotNr(obj.getPlotNr())
//			.nationalBuildingId(obj.getNationalBuildingId())
//			.historicPreservation(EnumeratedDto.of(CodeHistoricPreservation.getHistoricPreservation(obj.getHistoricPreservationId())))
//			.buildingYear(obj.getBuildingYear())
//			.street(obj.getStreet())
//			.zip(obj.getZip())
//			.city(obj.getCity())
//			.country(EnumeratedDto.of(CodeCountry.getCountry(obj.getCountryId())))
//			.geoAddress(obj.getGeoAddress())
//			.geoCoordinates(obj.getGeoCoordinates())
//			.geoZoom(obj.getGeoZoom())
//			.coverFotoId(obj.getCoverFotoId())
//			.currency(EnumeratedDto.of(CodeCurrency.getCurrency(obj.getCurrencyId())))
//			.volume(obj.getVolume())
//			.areaGross(obj.getAreaGross())
//			.areaNet(obj.getAreaNet())
//			.nrOfFloorsAboveGround(obj.getNrOfFloorsAboveGround())
//			.nrOfFloorsBelowGround(obj.getNrOfFloorsBelowGround())
//			.insuredValue(obj.getInsuredValue())
//			.insuredValueYear(obj.getInsuredValueYear())
//			.notInsuredValue(obj.getNotInsuredValue())
//			.notInsuredValueYear(obj.getNotInsuredValueYear())
//			.thirdPartyValue(obj.getThirdPartyValue())
//			.thirdPartyValueYear(obj.getThirdPartyValueYear())
//			// .ratingId(isNew ? ObjPartDtoBase.ServerNewIdPrefix + rating.getId() : String.valueOf(rating.getId()))
//			// .ratingSeqNr((int) obj.getRatingList().stream().filter(r -> this.isActiveRating(r)).count() - 1)
//			.partCatalog(EnumeratedDto.of(CodeBuildingPartCatalog.getPartCatalog(obj.getPartCatalogId())))
//			.maintenanceStrategy(EnumeratedDto.of(CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(obj.getMaintenanceStrategyId())))
//			.ratingStatus(EnumeratedDto.of(CodeBuildingRatingStatus.getRatingStatus(obj.getRatingStatusId())))
//			.ratingDate(obj.getRatingDate())
//			.ratingUser(ratingUser);
//		// @formatter:on
//		return dtoBuilder.build();
//	}

}
