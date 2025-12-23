package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.adapter.api.jsonapi.impl.ObjContactDtoAdapter;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("objAccountDtoAdapter")
public class ObjAccountDtoAdapter extends ObjDtoAdapterBase<ObjAccount, ObjAccountDto> {

	private ObjTenantDtoAdapter tenantDtoAdapter;

	private ObjContactRepository contactRepository = null;
	private ObjContactDtoAdapter contactDtoAdapter;

	private ObjDocumentRepository documentRepository = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	@Autowired
	void setTenantDtoAdapter(ObjTenantDtoAdapter tenantDtoAdapter) {
		this.tenantDtoAdapter = tenantDtoAdapter;
	}

	@Autowired
	void setContactRepository(ObjContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

	@Autowired
	@Lazy
		// break contact / account circular dependency
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

	public ObjTenantDto getTenantDto(Integer id) {
		return id != null ? this.tenantDtoAdapter.fromAggregate((ObjTenantFM) this.getTenant(id)) : null;
	}

	public ObjContactDto getContactDto(Integer id) {
		return id != null ? this.contactDtoAdapter.fromAggregate(this.contactRepository.get(id)) : null;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjAccountDto dto, ObjAccount obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);

			obj.setName(dto.getName());
			obj.setDescription(dto.getDescription());
			obj.setAccountType(dto.getAccountType() == null ? null : CodeAccountType.getAccountType(dto.getAccountType().getId()));
			obj.setClientSegment(dto.getClientSegment() == null ? null : CodeClientSegment.Enumeration.getClientSegment(dto.getClientSegment().getId()));
			obj.setReferenceCurrency(dto.getReferenceCurrency() == null ? null : CodeCurrency.Enumeration.getCurrency(dto.getReferenceCurrency().getId()));
			obj.setInflationRate(dto.getInflationRate());
			obj.setDiscountRate(dto.getDiscountRate());
			obj.setMainContactId(dto.getMainContactId());

		} finally {
			obj.getMeta().enableCalc();
			obj.getMeta().calcAll();
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
				.tenantInfoId((Integer) obj.getTenantId())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.of(obj.getAccountType()))
				.clientSegment(EnumeratedDto.of(obj.getClientSegment()))
				.referenceCurrency(EnumeratedDto.of(obj.getReferenceCurrency()))
				.inflationRate(obj.getInflationRate())
				.discountRate(obj.getDiscountRate())
				.mainContactId(obj.getMainContactId())
				.contactIdList(obj.getContactList().stream().map(id -> (Integer) id).toList())
				.logoId(obj.getLogoImageId())
				.build();
	}

//	@Override
//	public ObjAccountDto fromRecord(ObjAccountVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		return dtoBuilder
//				.tenantInfoId(obj.getTenantId())
//				.name(obj.getName())
//				.description(obj.getDescription())
//				.accountType(EnumeratedDto.of(CodeAccountTypeEnum.getAccountType(obj.getAccountTypeId())))
//				.clientSegment(EnumeratedDto.of(CodeClientSegmentEnum.getClientSegment(obj.getClientSegmentId())))
//				.referenceCurrency(EnumeratedDto.of(CodeCurrencyEnum.getCurrency(obj.getReferenceCurrencyId())))
//				.inflationRate(obj.getInflationRate())
//				.discountRate(obj.getDiscountRate())
//				.mainContactId(obj.getMainContactId())
//				.logoId(obj.getLogoImgId())
//				.build();
//	}

}
