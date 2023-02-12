
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountLoginDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;

@Component("objAccountLoginDtoAdapter")
public class ObjAccountLoginDtoAdapter
		extends ObjDtoAdapterBase<ObjAccount, ObjAccountVRecord, ObjAccountLoginDto> {

	protected ObjAccountLoginDtoAdapter(AppContext appContext) {
		super(appContext);
	}

	@Override
	public ObjAccountLoginDto fromAggregate(ObjAccount obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountLoginDto.ObjAccountLoginDtoBuilder<?, ?> dtoBuilder = ObjAccountLoginDto.builder()
				.appContext(this.getAppContext())
				.original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
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
