
package io.zeitwert.fm.lead.adapter.api.jsonapi.impl;

import io.zeitwert.fm.common.model.enums.CodeAreaEnum;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.FMDocDtoBridge;
import io.zeitwert.fm.lead.adapter.api.jsonapi.dto.DocLeadDto;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;
import io.zeitwert.fm.lead.model.enums.CodeLeadRatingEnum;
import io.zeitwert.fm.lead.model.enums.CodeLeadSourceEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.stream.Collectors;

public final class DocLeadDtoBridge extends FMDocDtoBridge<DocLead, DocLeadVRecord, DocLeadDto> {

	private static DocLeadDtoBridge instance;

	private static final CodeLeadSourceEnum leadSourceEnum = AppContext.getInstance()
			.getEnumeration(CodeLeadSourceEnum.class);
	private static final CodeLeadRatingEnum leadRatingEnum = AppContext.getInstance()
			.getEnumeration(CodeLeadRatingEnum.class);
	private static final CodeSalutationEnum salutationEnum = AppContext.getInstance()
			.getEnumeration(CodeSalutationEnum.class);
	private static final CodeTitleEnum titleEnum = AppContext.getInstance().getEnumeration(CodeTitleEnum.class);
	private static final CodeCountryEnum countryEnum = AppContext.getInstance().getEnumeration(CodeCountryEnum.class);
	private static final CodeAreaEnum areaEnum = AppContext.getInstance().getEnumeration(CodeAreaEnum.class);

	private DocLeadDtoBridge() {
	}

	public static final DocLeadDtoBridge getInstance() {
		if (instance == null) {
			instance = new DocLeadDtoBridge();
		}
		return instance;
	}

	@Override
	public void toAggregate(DocLeadDto dto, DocLead doc) {
		super.toAggregate(dto, doc);
		doc.setAccountId(dto.getAccountId());
		doc.setSubject(dto.getSubject());
		if (dto.getAreas() != null) {
			doc.clearAreas();
			dto.getAreas().forEach(area -> doc.addArea(areaEnum.getItem(area.getId())));
		}
		doc.setLeadSource(dto.getLeadSource() == null ? null : leadSourceEnum.getItem(dto.getLeadSource().getId()));
		doc.setLeadRating(dto.getLeadRating() == null ? null : leadRatingEnum.getItem(dto.getLeadRating().getId()));
		doc.setDescription(dto.getDescription());
		doc.setSalutation(dto.getSalutation() == null ? null : salutationEnum.getItem(dto.getSalutation().getId()));
		doc.setTitle(dto.getTitle() == null ? null : titleEnum.getItem(dto.getTitle().getId()));
		doc.setFirstName(dto.getFirstName());
		doc.setLastName(dto.getLastName());
		doc.setPhone(dto.getPhone());
		doc.setMobile(dto.getMobile());
		doc.setEmail(dto.getEmail());
		doc.setStreet(dto.getStreet());
		doc.setZip(dto.getZip());
		doc.setCity(dto.getCity());
		doc.setState(dto.getState());
		doc.setCountry(dto.getCountry() == null ? null : countryEnum.getItem(dto.getCountry().getId()));
		doc.setContactId(dto.getContactId());
	}

	@Override
	public DocLeadDto fromAggregate(DocLead doc, SessionInfo sessionInfo) {
		if (doc == null) {
			return null;
		}
		DocLeadDto.DocLeadDtoBuilder<?, ?> dtoBuilder = DocLeadDto.builder().original(doc);
		this.fromAggregate(dtoBuilder, doc, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.accountId(doc.getAccountId())
			.subject(doc.getSubject())
			.areas(doc.getAreas().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.leadSource(EnumeratedDto.fromEnum(doc.getLeadSource()))
			.leadRating(EnumeratedDto.fromEnum(doc.getLeadRating()))
			.description(doc.getDescription())
			.salutation(EnumeratedDto.fromEnum(doc.getSalutation()))
			.title(EnumeratedDto.fromEnum(doc.getTitle()))
			.firstName(doc.getFirstName())
			.lastName(doc.getLastName())
			.phone(doc.getPhone())
			.mobile(doc.getMobile())
			.email(doc.getEmail())
			.street(doc.getStreet())
			.zip(doc.getZip())
			.city(doc.getCity())
			.state(doc.getState())
			.country(EnumeratedDto.fromEnum(doc.getCountry()))
			.contactId(doc.getContactId())
			.build();
		// @formatter:on
	}

	@Override
	public DocLeadDto fromRecord(DocLeadVRecord doc, SessionInfo sessionInfo) {
		if (doc == null) {
			return null;
		}
		DocLeadDto.DocLeadDtoBuilder<?, ?> dtoBuilder = DocLeadDto.builder().original(null);
		this.fromRecord(dtoBuilder, doc, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.accountId(doc.getAccountId())
			.subject(doc.getSubject())
			//.areas(doc.getAreas().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.leadSource(EnumeratedDto.fromEnum(CodeLeadSourceEnum.getLeadSource(doc.getLeadSourceId())))
			.leadRating(EnumeratedDto.fromEnum(CodeLeadRatingEnum.getLeadRating(doc.getLeadRatingId())))
			.description(doc.getDescription())
			.salutation(EnumeratedDto.fromEnum(CodeSalutationEnum.getSalutation(doc.getSalutationId())))
			.title(EnumeratedDto.fromEnum(CodeTitleEnum.getTitle(doc.getTitleId())))
			.firstName(doc.getFirstName())
			.lastName(doc.getLastName())
			.phone(doc.getPhone())
			.mobile(doc.getMobile())
			.email(doc.getEmail())
			.street(doc.getStreet())
			.zip(doc.getZip())
			.city(doc.getCity())
			.state(doc.getState())
			.country(EnumeratedDto.fromEnum(CodeCountryEnum.getCountry(doc.getCountryId())))
			.contactId(doc.getContactId())
			.build();
		// @formatter:on
	}

}
