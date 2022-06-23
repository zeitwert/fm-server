
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import java.util.stream.Collectors;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeAreaEnum;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoBridge;

public final class ObjAccountDtoBridge extends FMObjDtoBridge<ObjAccount, ObjAccountVRecord, ObjAccountDto> {

	private static ObjAccountDtoBridge instance;

	private ObjAccountDtoBridge() {
	}

	public static final ObjAccountDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjAccountDtoBridge();
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
			if (dto.getAreas() != null) {
				obj.clearAreaSet();
				dto.getAreas().forEach(area -> obj.addArea(CodeAreaEnum.getArea(area.getId())));
			}
			obj.setMainContactId(dto.getMainContactId());

		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
		}
	}

	@Override
	public ObjAccountDto fromAggregate(ObjAccount obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj, sessionInfo);
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

	@Override
	public ObjAccountDto fromRecord(ObjAccountVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjAccountDto.ObjAccountDtoBuilder<?, ?> dtoBuilder = ObjAccountDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj, sessionInfo);
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
