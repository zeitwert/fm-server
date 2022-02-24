
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

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
import io.zeitwert.fm.building.adapter.api.jsonapi.ObjBuildingApiRepository;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.building.service.api.ProjectionService;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl extends ResourceRepositoryBase<ObjBuildingDto, Integer>
		implements ObjBuildingApiRepository {

	private final ObjBuildingRepository repository;
	private final SessionInfo sessionInfo;
	private final ProjectionService projectionService;

	@Autowired
	public ObjBuildingApiRepositoryImpl(final ObjBuildingRepository repository, final SessionInfo sessionInfo,
			final ProjectionService projectionService) {
		super(ObjBuildingDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
		this.projectionService = projectionService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjBuildingDto create(ObjBuildingDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation");
		}
		ObjBuilding obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjBuildingDto.fromObj(obj, this.sessionInfo, this.projectionService);
	}

	@Override
	public ObjBuildingDto findOne(Integer objId, QuerySpec querySpec) {
		Optional<ObjBuilding> maybeBuilding = this.repository.get(this.sessionInfo, objId);
		if (!maybeBuilding.isPresent()) {
			throw new ResourceNotFoundException("Resource not found!");
		}
		return ObjBuildingDto.fromObj(maybeBuilding.get(), this.sessionInfo, this.projectionService);
	}

	@Override
	public ResourceList<ObjBuildingDto> findAll(QuerySpec querySpec) {
		List<ObjBuildingVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjBuildingDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjBuildingDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjBuildingDto save(ObjBuildingDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjBuilding obj = this.repository.get(this.sessionInfo, dto.getId()).get();
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjBuildingDto.fromObj(obj, this.sessionInfo, this.projectionService);
	}

}
