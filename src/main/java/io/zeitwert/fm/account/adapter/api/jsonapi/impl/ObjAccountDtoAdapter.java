
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoAdapterBase;

public final class ObjAccountDtoAdapter extends FMObjDtoAdapterBase<ObjAccount, ObjAccountVRecord, ObjAccountDto> {

	private static ObjAccountDtoAdapter instance;

	private ObjAccountDtoAdapter() {
	}

	public static final ObjAccountDtoAdapter getInstance() {
		if (instance == null) {
			instance = new ObjAccountDtoAdapter();
		}
		return instance;
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
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenantInfoId(obj.getTenantId())
				.key(obj.getKey())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.fromEnum(obj.getAccountType()))
				.clientSegment(EnumeratedDto.fromEnum(obj.getClientSegment()))
				.referenceCurrency(EnumeratedDto.fromEnum(obj.getReferenceCurrency()))
				.inflationRate(obj.getInflationRate())
				.mainContactId(obj.getMainContactId())
				.build();
	}

	@Override
	public ObjAccountDto fromRecord(ObjAccountVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.tenantInfoId(obj.getTenantId())
				.key(obj.getIntlKey())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.fromEnum(CodeAccountTypeEnum.getAccountType(obj.getAccountTypeId())))
				.clientSegment(EnumeratedDto.fromEnum(CodeClientSegmentEnum.getClientSegment(obj.getClientSegmentId())))
				.referenceCurrency(EnumeratedDto.fromEnum(CodeCurrencyEnum.getCurrency(obj.getReferenceCurrencyId())))
				.inflationRate(obj.getInflationRate())
				.mainContactId(obj.getMainContactId())
				.build();
	}

}
