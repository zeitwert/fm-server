
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;

@Component("objAccountDtoAdapter")
public class ObjAccountDtoAdapter extends ObjDtoAdapterBase<ObjAccount, ObjAccountVRecord, ObjAccountDto> {

	protected ObjAccountDtoAdapter(AppContext appContext) {
		super(appContext);
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
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder()
		.appContext(this.getAppContext())
		.original(obj);
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
				.build();
	}

	@Override
	public ObjAccountDto fromRecord(ObjAccountVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder()
				.appContext(this.getAppContext())
				.original(null);
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
				.build();
	}

}
