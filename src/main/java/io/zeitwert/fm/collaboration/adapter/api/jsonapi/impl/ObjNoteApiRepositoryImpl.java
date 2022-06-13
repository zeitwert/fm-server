
package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.ObjNoteApiRepository;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objNoteApiRepository")
public class ObjNoteApiRepositoryImpl extends ResourceRepositoryBase<ObjNoteDto, Integer>
		implements ObjNoteApiRepository {

	private final ObjNoteRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public ObjNoteApiRepositoryImpl(final ObjNoteRepository repository, SessionInfo sessionInfo) {
		super(ObjNoteDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjNoteDto create(ObjNoteDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		ObjNote obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjNoteDto.fromObj(obj, this.sessionInfo);
	}

	@Override
	public ObjNoteDto findOne(Integer objId, QuerySpec querySpec) {
		ObjNote note = this.repository.get(this.sessionInfo, objId);
		return ObjNoteDto.fromObj(note, this.sessionInfo);
	}

	@Override
	public ResourceList<ObjNoteDto> findAll(QuerySpec querySpec) {
		List<ObjNoteVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjNoteDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjNoteDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjNoteDto save(ObjNoteDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjNote obj = this.repository.get(this.sessionInfo, dto.getId());
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjNoteDto.fromObj(obj, this.sessionInfo);
	}

}
