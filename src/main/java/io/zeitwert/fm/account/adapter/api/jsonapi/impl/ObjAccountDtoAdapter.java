
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.service.api.ObjContactCache;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.service.api.ObjDocumentCache;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenantFM;

@Component("objAccountDtoAdapter")
public class ObjAccountDtoAdapter extends ObjDtoAdapterBase<ObjAccount, ObjAccountVRecord, ObjAccountDto> {

	private ObjTenantDtoAdapter tenantDtoAdapter;

	private ObjContactCache contactCache = null;
	private ObjContactDtoAdapter contactDtoAdapter;

	private ObjDocumentCache documentCache = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	@Autowired
	void setTenantDtoAdapter(ObjTenantDtoAdapter tenantDtoAdapter) {
		this.tenantDtoAdapter = tenantDtoAdapter;
	}

	@Autowired
	void setContactCache(ObjContactCache contactCache) {
		this.contactCache = contactCache;
	}

	@Autowired
	@Lazy // break contact / account circular dependency
	void setContactDtoAdapter(ObjContactDtoAdapter contactDtoAdapter) {
		this.contactDtoAdapter = contactDtoAdapter;
	}

	@Autowired
	void setDocumentCache(ObjDocumentCache documentCache) {
		this.documentCache = documentCache;
	}

	@Autowired
	void setDocumentDtoAdapter(ObjDocumentDtoAdapter documentDtoAdapter) {
		this.documentDtoAdapter = documentDtoAdapter;
	}

	public ObjTenantDto getTenantDto(Integer id) {
		return id != null ? this.tenantDtoAdapter.fromAggregate((ObjTenantFM) this.getTenant(id)) : null;
	}

	public ObjContactDto getContactDto(Integer id) {
		return id != null ? this.contactDtoAdapter.fromAggregate(this.contactCache.get(id)) : null;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentCache.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjAccountDto dto, ObjAccount obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			obj.setName(dto.getName());
			obj.setDescription(dto.getDescription());
			obj.setAccountType(
					dto.getAccountType() == null ? null : CodeAccountTypeEnum.getAccountType(dto.getAccountType().getId()));
			obj.setClientSegment(
					dto.getClientSegment() == null ? null
							: CodeClientSegmentEnum.getClientSegment(dto.getClientSegment().getId()));
			obj.setReferenceCurrency(
					dto.getReferenceCurrency() == null ? null : CodeCurrencyEnum.getCurrency(dto.getReferenceCurrency().getId()));
			obj.setInflationRate(dto.getInflationRate());
			obj.setMainContactId(dto.getMainContactId());

		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
		}
	}

	@Override
	public ObjAccountDto fromAggregate(ObjAccount obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenantInfoId(obj.getTenantId())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.fromEnum(obj.getAccountType()))
				.clientSegment(EnumeratedDto.fromEnum(obj.getClientSegment()))
				.referenceCurrency(EnumeratedDto.fromEnum(obj.getReferenceCurrency()))
				.inflationRate(obj.getInflationRate())
				.mainContactId(obj.getMainContactId())
				.contactIdList(obj.getContacts().stream().map(c -> c.getId()).toList())
				.logoId(obj.getLogoImageId())
				.build();
	}

	@Override
	public ObjAccountDto fromRecord(ObjAccountVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder();
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.tenantInfoId(obj.getTenantId())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.fromEnum(CodeAccountTypeEnum.getAccountType(obj.getAccountTypeId())))
				.clientSegment(EnumeratedDto.fromEnum(CodeClientSegmentEnum.getClientSegment(obj.getClientSegmentId())))
				.referenceCurrency(EnumeratedDto.fromEnum(CodeCurrencyEnum.getCurrency(obj.getReferenceCurrencyId())))
				.inflationRate(obj.getInflationRate())
				.mainContactId(obj.getMainContactId())
				.logoId(obj.getLogoImgId())
				.build();
	}

}
