
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objPortfolioApiRepository")
public class ObjPortfolioApiRepositoryImpl extends ResourceRepositoryBase<ObjPortfolioDto, Integer>
		implements ObjPortfolioApiRepository {

	private final ObjPortfolioRepository repository;
	private final SessionInfo sessionInfo;

	@Autowired
	public ObjPortfolioApiRepositoryImpl(final ObjPortfolioRepository repository, SessionInfo sessionInfo) {
		super(ObjPortfolioDto.class);
		this.repository = repository;
		this.sessionInfo = sessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjPortfolioDto create(ObjPortfolioDto dto) {
		if (dto.getId() != null) {
			throw new BadRequestException("Cannot specify id on creation (" + dto.getId() + ")");
		}
		ObjPortfolio obj = this.repository.create(this.sessionInfo);
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjPortfolioDto.fromObj(obj, this.sessionInfo);
	}

	@Override
	public ObjPortfolioDto findOne(Integer objId, QuerySpec querySpec) {
		ObjPortfolio portfolio = this.repository.get(this.sessionInfo, objId);
		return ObjPortfolioDto.fromObj(portfolio, this.sessionInfo);
	}

	@Override
	public ResourceList<ObjPortfolioDto> findAll(QuerySpec querySpec) {
		List<ObjPortfolioVRecord> itemList = this.repository.find(this.sessionInfo, querySpec);
		ResourceList<ObjPortfolioDto> list = new DefaultResourceList<>();
		list.addAll(itemList.stream().map(item -> ObjPortfolioDto.fromRecord(item, this.sessionInfo)).toList());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjPortfolioDto save(ObjPortfolioDto dto) {
		if (dto.getId() == null) {
			throw new BadRequestException("Can only save existing object (missing id)");
		}
		ObjPortfolio obj = this.repository.get(this.sessionInfo, dto.getId());
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjPortfolioDto.fromObj(obj, this.sessionInfo);
	}

}
