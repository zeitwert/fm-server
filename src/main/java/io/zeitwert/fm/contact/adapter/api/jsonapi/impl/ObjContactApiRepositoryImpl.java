
package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.fm.contact.adapter.api.jsonapi.ObjContactApiRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objContactApiRepository")
public class ObjContactApiRepositoryImpl extends ResourceRepositoryBase<ObjContactDto, Integer>
		implements ObjContactApiRepository {

	private final ObjContactRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public ObjContactApiRepositoryImpl(final ObjContactRepository repository, SessionInfo sessionInfo) {
		super(ObjContactDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjContactDto create(ObjContactDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation");
		}
		ObjContact obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjContactDto.fromObj(obj, this.sessionInfo);
	}

	@Override
	public ObjContactDto findOne(Integer objId, QuerySpec querySpec) {
		Optional<ObjContact> maybeContact = this.repository.get(this.sessionInfo, objId);
		if (!maybeContact.isPresent()) {
			throw new ResourceNotFoundException("Resource not found!");
		}
		ObjContactDto c = ObjContactDto.fromObj(maybeContact.get(), this.sessionInfo);
		return c;
	}

	@Override
	public ResourceList<ObjContactDto> findAll(QuerySpec querySpec) {
		List<ObjContactVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjContactDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjContactDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjContactDto save(ObjContactDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjContact obj = this.repository.get(this.sessionInfo, dto.getId()).get();
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjContactDto.fromObj(obj, this.sessionInfo);
	}

}
