package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountLoginDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("objAccountLoginDtoAdapter")
public class ObjAccountLoginDtoAdapter
		extends ObjDtoAdapterBase<ObjAccount, ObjAccountLoginDto> {

	private ObjDocumentRepository documentRepository = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	@Autowired
	void setDocumentRepository(ObjDocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@Autowired
	void setDocumentDtoAdapter(ObjDocumentDtoAdapter documentDtoAdapter) {
		this.documentDtoAdapter = documentDtoAdapter;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentRepository.get(id)) : null;
	}

	@Override
	public ObjAccountLoginDto fromAggregate(ObjAccount obj) {
		if (obj == null) {
			return null;
		}
		ObjAccountLoginDto.ObjAccountLoginDtoBuilder<?, ?> dtoBuilder = ObjAccountLoginDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.name(obj.getName())
				.description(obj.getDescription())
				.accountType(EnumeratedDto.of(obj.getAccountType()))
				.clientSegment(EnumeratedDto.of(obj.getClientSegment()))
				.referenceCurrency(EnumeratedDto.of(obj.getReferenceCurrency()))
				.logoId((Integer) obj.getLogoImageId())
				.build();
	}

//	@Override
//	public ObjAccountLoginDto fromRecord(ObjAccountVRecord obj) {
//		return null;
//	}

}
