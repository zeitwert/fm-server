
package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.dms.adapter.api.jsonapi.ObjDocumentApiRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;

@Controller("objDocumentApiRepository")
public class ObjDocumentApiRepositoryImpl extends ResourceRepositoryBase<ObjDocumentDto, Integer>
		implements ObjDocumentApiRepository {

	private final ObjDocumentRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public ObjDocumentApiRepositoryImpl(final ObjDocumentRepository repository, SessionInfo sessionInfo) {
		super(ObjDocumentDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjDocumentDto create(ObjDocumentDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		ObjDocument obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjDocumentDto.fromObj(obj, this.sessionInfo);
	}

	@Override
	public ObjDocumentDto findOne(Integer objId, QuerySpec querySpec) {
		ObjDocument document = this.repository.get(this.sessionInfo, objId);
		return ObjDocumentDto.fromObj(document, this.sessionInfo);
	}

	@Override
	public ResourceList<ObjDocumentDto> findAll(QuerySpec querySpec) {
		List<ObjDocumentVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjDocumentDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjDocumentDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjDocumentDto save(ObjDocumentDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjDocument obj = this.repository.get(this.sessionInfo, dto.getId());
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjDocumentDto.fromObj(obj, this.sessionInfo);
	}

}
