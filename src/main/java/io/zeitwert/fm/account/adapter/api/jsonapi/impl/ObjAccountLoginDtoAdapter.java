
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountLoginDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoAdapter;

public final class ObjAccountLoginDtoAdapter
		extends FMObjDtoAdapter<ObjAccount, ObjAccountVRecord, ObjAccountLoginDto> {

	private static ObjAccountLoginDtoAdapter instance;

	private ObjAccountLoginDtoAdapter() {
	}

	public static final ObjAccountLoginDtoAdapter getInstance() {
		if (instance == null) {
			instance = new ObjAccountLoginDtoAdapter();
		}
		return instance;
	}

	@Override
	public ObjAccountLoginDto fromAggregate(ObjAccount obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountLoginDto.ObjAccountLoginDtoBuilder<?, ?> dtoBuilder = ObjAccountLoginDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.key(obj.getKey())
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.fromEnum(obj.getAccountType()))
				.clientSegment(EnumeratedDto.fromEnum(obj.getClientSegment()))
				.referenceCurrency(EnumeratedDto.fromEnum(obj.getReferenceCurrency()))
				.inflationRate(obj.getInflationRate())
				.build();
	}

	@Override
	public ObjAccountLoginDto fromRecord(ObjAccountVRecord obj) {
		return null;
	}

}
