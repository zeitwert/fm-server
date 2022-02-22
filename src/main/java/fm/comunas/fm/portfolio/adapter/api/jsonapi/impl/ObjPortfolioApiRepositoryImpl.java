
package fm.comunas.fm.portfolio.adapter.api.jsonapi.impl;

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
import fm.comunas.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import fm.comunas.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import fm.comunas.fm.portfolio.model.ObjPortfolio;
import fm.comunas.fm.portfolio.model.ObjPortfolioRepository;
import fm.comunas.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import fm.comunas.ddd.session.model.SessionInfo;

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
		Optional<ObjPortfolio> maybePortfolio = this.repository.get(this.sessionInfo, objId);
		if (!maybePortfolio.isPresent()) {
			throw new ResourceNotFoundException("Resource not found!");
		}
		return ObjPortfolioDto.fromObj(maybePortfolio.get(), this.sessionInfo);
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
		ObjPortfolio obj = this.repository.get(this.sessionInfo, dto.getId()).get();
		dto.toObj(obj);
		this.repository.store(obj);
		return ObjPortfolioDto.fromObj(obj, this.sessionInfo);
	}

}
