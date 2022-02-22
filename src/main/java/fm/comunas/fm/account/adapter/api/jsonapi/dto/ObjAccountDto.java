package fm.comunas.fm.account.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.account.model.db.tables.records.ObjAccountVRecord;
import fm.comunas.fm.account.model.enums.CodeClientSegmentEnum;
import fm.comunas.fm.account.model.enums.CodeAccountTypeEnum;
import fm.comunas.fm.common.model.enums.CodeAreaEnum;
import fm.comunas.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import fm.comunas.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import fm.comunas.ddd.common.model.enums.CodeCurrencyEnum;
import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "account", resourcePath = "account/accounts", deletable = false)
public class ObjAccountDto extends FMObjDtoBase<ObjAccount> {

	@JsonIgnore
	private ObjContactDto mainContactDto;

	@JsonIgnore
	private List<ObjContactDto> contactsDto;

	private String name;
	private String description;
	private EnumeratedDto accountType;
	private EnumeratedDto clientSegment;
	private EnumeratedDto referenceCurrency;
	private Set<EnumeratedDto> areas;

	@JsonApiRelationId
	private Integer mainContactId;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjContactDto getMainContact() {
		if (this.mainContactDto == null) {
			if (this.getOriginal() != null) {
				this.mainContactDto = ObjContactDto.fromObj(this.getOriginal().getMainContact(), this.sessionInfo);
			} else if (this.mainContactId != null) {
			}
		}
		return this.mainContactDto;
	}

	// Crnk needs to see this to set mainContractId
	public void setMainContact(ObjContactDto mainContact) {
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public List<? extends ObjContactDto> getContacts() {
		if (this.contactsDto == null) {
			if (this.getOriginal() != null) {
				this.contactsDto = this.getOriginal().getContacts().stream()
						.map(c -> ObjContactDto.fromObj(c, this.sessionInfo)).toList();
			} else {
			}
		}
		return this.contactsDto;
	}

	@Override
	public void toObj(ObjAccount obj) {
		super.toObj(obj);
		obj.setName(name);
		obj.setDescription(description);
		obj.setAccountType(accountType == null ? null : CodeAccountTypeEnum.getAccountType(accountType.getId()));
		obj.setClientSegment(clientSegment == null ? null : CodeClientSegmentEnum.getClientSegment(clientSegment.getId()));
		obj.setReferenceCurrency(
				referenceCurrency == null ? null : CodeCurrencyEnum.getCurrency(referenceCurrency.getId()));
		if (areas != null) {
			obj.clearAreaSet();
			areas.forEach(area -> obj.addArea(CodeAreaEnum.getArea(area.getId())));
		}
		obj.setMainContactId(mainContactId);
	}

	public static ObjAccountDto fromObj(ObjAccount obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(obj);
		FMObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.description(obj.getDescription())
			.accountType(EnumeratedDto.fromEnum(obj.getAccountType()))
			.clientSegment(EnumeratedDto.fromEnum(obj.getClientSegment()))
			.referenceCurrency(EnumeratedDto.fromEnum(obj.getReferenceCurrency()))
			.areas(obj.getAreaSet().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.mainContactId(obj.getMainContactId())
			.build();
		// @formatter:on
	}

	public static ObjAccountDto fromRecord(ObjAccountVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(null);
		FMObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.description(obj.getDescription())
			.accountType(EnumeratedDto.fromEnum(CodeAccountTypeEnum.getAccountType(obj.getAccountTypeId())))
			.clientSegment(EnumeratedDto.fromEnum(CodeClientSegmentEnum.getClientSegment(obj.getClientSegmentId())))
			.referenceCurrency(EnumeratedDto.fromEnum(CodeCurrencyEnum.getCurrency(obj.getReferenceCurrencyId())))
			//.areas(obj.getAreaSet().stream().map(a -> EnumeratedDto.fromEnum(a)).collect(Collectors.toSet()))
			.mainContactId(obj.getMainContactId())
			.build();
		// @formatter:on
	}

}
