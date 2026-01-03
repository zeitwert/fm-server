package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.DtoUtils;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingPartRatingDto;
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
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("objBuildingDtoAdapter")
public class ObjBuildingDtoAdapter extends ObjDtoAdapterBase<ObjBuilding, ObjBuildingDto> {

	private final ObjUserDtoAdapter userDtoAdapter;
	private SessionContext sessionContext;
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
	public void setRequestContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
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

	public ObjAccountDto getAccountDto(String id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(DtoUtils.idFromString(id))) : null;
	}

	public ObjContactDto getContactDto(String id) {
		return id != null ? this.contactDtoAdapter.fromAggregate(this.contactRepository.get(DtoUtils.idFromString(id))) : null;
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
			obj.setAccountId(DtoUtils.idFromString(dto.getAccountId()));
			obj.setName(dto.getName());
			obj.setDescription(dto.getDescription());
			obj.setBuildingNr(dto.getBuildingNr());
			obj.setInsuranceNr(dto.getInsuranceNr());
			obj.setPlotNr(dto.getPlotNr());
			obj.setNationalBuildingId(dto.getNationalBuildingId());
			obj.setHistoricPreservation(dto.getHistoricPreservation() == null ? null : CodeHistoricPreservation.getHistoricPreservation(dto.getHistoricPreservation().getId()));

			obj.setBuildingType(dto.getBuildingType() == null ? null : CodeBuildingType.getBuildingType(dto.getBuildingType().getId()));
			obj.setBuildingSubType(dto.getBuildingSubType() == null ? null : CodeBuildingSubType.getBuildingSubType(dto.getBuildingSubType().getId()));
			obj.setBuildingYear(dto.getBuildingYear());
			obj.setStreet(dto.getStreet());
			obj.setZip(dto.getZip());
			obj.setCity(dto.getCity());
			obj.setCountry(dto.getCountry() == null ? null : CodeCountry.getCountry(dto.getCountry().getId()));
			obj.setGeoAddress(dto.getGeoAddress());
			obj.setGeoCoordinates(dto.getGeoCoordinates());
			obj.setGeoZoom(dto.getGeoZoom());
			obj.setCurrency(dto.getCurrency() == null ? null : CodeCurrency.Enumeration.getCurrency(dto.getCurrency().getId()));
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
				obj.getContactSet().clear();
				dto.getContactIds().forEach(id -> obj.getContactSet().add(DtoUtils.idFromString(id)));
			}

			if (dto.getMeta() != null && dto.getMeta().hasOperation(ObjBuildingDto.AddRatingOperation)) {
				obj.addRating(this.sessionContext.getUser(), this.sessionContext.getCurrentTime());
			} else if (dto.getCurrentRating() != null && dto.getCurrentRating().getSeqNr() != null && dto.getCurrentRating().getSeqNr() >= 0) {
				ObjBuildingPartRatingDto ratingDto = dto.getCurrentRating();
				final ObjBuildingPartRating rating =
					obj.getCurrentRating() == null ||
					ratingDto.getSeqNr() >= obj.getRatingList().size()
						? obj.addRating(this.sessionContext.getUser(), this.sessionContext.getCurrentTime())
						: obj.getCurrentRating();
				ratingDto.toPart(rating);
				Object userId = ratingDto.getRatingUser() == null ? null : DtoUtils.idFromString(ratingDto.getRatingUser().getId());
				rating.setRatingUser(userId == null ? null : this.getUser(userId));
				if (ratingDto.getElements() != null) {
					ratingDto.getElements().forEach(elementDto -> {
						ObjBuildingPartElementRating element = null;
						if (elementDto.getPartId() == null) {
//							TODO assertThis(elementDto.getBuildingPart() != null, "valid dto buildingPart");
							CodeBuildingPart buildingPart = CodeBuildingPart.Enumeration.getBuildingPart(elementDto.getBuildingPart().getId());
//							TODO assertThis(rating.getElement(buildingPart) != null, "valid rating buildingPart");
							element = rating.getElement(buildingPart);
						} else {
							element = rating.getElementList().getById(elementDto.getPartId());
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
				.accountId(obj.getAccountId() != null ? DtoUtils.idToString(obj.getAccountId()) : null)
				.buildingType(EnumeratedDto.of(obj.getBuildingType()))
				.buildingSubType(EnumeratedDto.of(obj.getBuildingSubType()))
				.name(obj.getName())
				.description(obj.getDescription())
				.buildingNr(obj.getBuildingNr())
				.insuranceNr(obj.getInsuranceNr())
				.plotNr(obj.getPlotNr())
				.nationalBuildingId(obj.getNationalBuildingId())
				.historicPreservation(EnumeratedDto.of(obj.getHistoricPreservation()))
				.buildingYear(obj.getBuildingYear())
				.street(obj.getStreet())
				.zip(obj.getZip())
				.city(obj.getCity())
				.country(EnumeratedDto.of(obj.getCountry()))
				.geoAddress(obj.getGeoAddress())
				.geoCoordinates(obj.getGeoCoordinates())
				.geoZoom(obj.getGeoZoom())
				.coverFotoId((Integer) obj.getCoverFotoId())
				.currency(EnumeratedDto.of(obj.getCurrency()))
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
				.contactIds(obj.getContactSet().stream().map(DtoUtils::idToString).collect(java.util.stream.Collectors.toSet()));
		if (obj.getCurrentRating() != null) {
			int seqNr = (int) obj.getRatingList().stream().filter(this::isActiveRating).count() - 1;
			dtoBuilder.currentRating(ObjBuildingPartRatingDto.fromPart(obj.getCurrentRating(), this.userDtoAdapter, seqNr));
		}
		return dtoBuilder.build();
	}

	private boolean isActiveRating(ObjBuildingPartRating rating) {
		return rating.getRatingStatus() == null || rating.getRatingStatus() != CodeBuildingRatingStatus.DISCARD;
	}

}
