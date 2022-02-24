package io.zeitwert.fm.lead.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.common.model.enums.CodeAreaEnum;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.FMDocDtoBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;
import io.zeitwert.fm.lead.model.enums.CodeLeadRatingEnum;
import io.zeitwert.fm.lead.model.enums.CodeLeadSourceEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "lead", resourcePath = "lead/leads", deletable = false)
public class DocLeadDto extends FMDocDtoBase<DocLead> {

	private static final CodeLeadSourceEnum leadSourceEnum = AppContext.getInstance()
			.getEnumeration(CodeLeadSourceEnum.class);
	private static final CodeLeadRatingEnum leadRatingEnum = AppContext.getInstance()
			.getEnumeration(CodeLeadRatingEnum.class);
	private static final CodeSalutationEnum salutationEnum = AppContext.getInstance()
			.getEnumeration(CodeSalutationEnum.class);
	private static final CodeTitleEnum titleEnum = AppContext.getInstance().getEnumeration(CodeTitleEnum.class);
	private static final CodeCountryEnum countryEnum = AppContext.getInstance().getEnumeration(CodeCountryEnum.class);
	private static final CodeAreaEnum areaEnum = AppContext.getInstance().getEnumeration(CodeAreaEnum.class);

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	@JsonApiRelationId
	private Integer contactId;

	@JsonIgnore
	private ObjContactDto contactDto;

	private String subject;
	private String description;
	private Set<EnumeratedDto> areas;
	private EnumeratedDto leadSource;
	private EnumeratedDto leadRating;
	private EnumeratedDto salutation;
	private EnumeratedDto title;
	private String firstName;
	private String lastName;
	private String phone;
	private String mobile;
	private String email;
	private String street;
	private String zip;
	private String city;
	private String state;
	private EnumeratedDto country;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			this.accountDto = ObjAccountDto.fromObj(this.getOriginal().getAccount(), this.sessionInfo);
		}
		return this.accountDto;
	}

	// Crnk needs to see this to set accountId
	public void setAccount(ObjAccountDto account) {
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjContactDto getContact() {
		if (this.contactDto == null) {
			this.contactDto = ObjContactDto.fromObj(this.getOriginal().getContact(), this.sessionInfo);
		}
		return this.contactDto;
	}

	// Crnk needs to see this to set contactId
	public void setContact(ObjContactDto contact) {
	}

	@Override
	public void toDoc(DocLead doc) {
		super.toDoc(doc);
		doc.setAccountId(accountId);
		doc.setSubject(subject);
		if (areas != null) {
			doc.clearAreas();
			areas.forEach(area -> doc.addArea(areaEnum.getItem(area.getId())));
		}
		doc.setLeadSource(leadSource == null ? null : leadSourceEnum.getItem(leadSource.getId()));
		doc.setLeadRating(leadRating == null ? null : leadRatingEnum.getItem(leadRating.getId()));
		doc.setDescription(description);
		doc.setSalutation(salutation == null ? null : salutationEnum.getItem(salutation.getId()));
		doc.setTitle(title == null ? null : titleEnum.getItem(title.getId()));
		doc.setFirstName(firstName);
		doc.setLastName(lastName);
		doc.setPhone(phone);
		doc.setMobile(mobile);
		doc.setEmail(email);
		doc.setStreet(street);
		doc.setZip(zip);
		doc.setCity(city);
		doc.setState(state);
		doc.setCountry(country == null ? null : countryEnum.getItem(country.getId()));
		doc.setContactId(contactId);
	}

	public static DocLeadDto fromDoc(DocLead doc, SessionInfo sessionInfo) {
		if (doc == null) {
			return null;
		}
		DocLeadDtoBuilder<?, ?> dtoBuilder = DocLeadDto.builder().original(doc);
		FMDocDtoBase.fromDoc(dtoBuilder, doc, sessionInfo);
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

	public static DocLeadDto fromRecord(DocLeadVRecord doc, SessionInfo sessionInfo) {
		if (doc == null) {
			return null;
		}
		DocLeadDtoBuilder<?, ?> dtoBuilder = DocLeadDto.builder().original(null);
		FMDocDtoBase.fromRecord(dtoBuilder, doc, sessionInfo);
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
