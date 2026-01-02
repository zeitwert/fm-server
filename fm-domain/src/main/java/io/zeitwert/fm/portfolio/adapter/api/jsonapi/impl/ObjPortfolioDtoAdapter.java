package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import dddrive.app.obj.model.Obj;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.obj.model.FMObjVRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component("objPortfolioDtoAdapter")
public class ObjPortfolioDtoAdapter
		extends ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioDto> {

	private FMObjVRepository objRepository = null;

	private ObjAccountRepository accountRepository = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

	@Autowired
	void setObjRepository(FMObjVRepository objRepository) {
		this.objRepository = objRepository;
	}

	@Autowired
	void setAccountRepository(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(String id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(Integer.parseInt(id))) : null;
	}

	@Override
	public void toAggregate(ObjPortfolioDto dto, ObjPortfolio pf) {
		try {
			pf.getMeta().disableCalc();
			super.toAggregate(dto, pf);

			pf.setName(dto.getName());
			pf.setDescription(dto.getDescription());
			pf.setPortfolioNr(dto.getPortfolioNr());
			pf.setAccountId(Integer.parseInt(dto.getAccountId()));
			// TODO prevent calculation during insert
			if (dto.getIncludes() != null) {
				pf.getIncludeSet().clear();
				dto.getIncludes().forEach(item -> {
					Integer id = Integer.parseInt(item.getId());
					pf.getIncludeSet().add(id);
				});
			}
			if (dto.getExcludes() != null) {
				pf.getExcludeSet().clear();
				dto.getExcludes().forEach(item -> {
					Integer id = Integer.parseInt(item.getId());
					pf.getExcludeSet().add(id);
				});
			}

		} finally {
			pf.getMeta().enableCalc();
			pf.getMeta().calcAll();
		}
	}

	@Override
	public ObjPortfolioDto fromAggregate(ObjPortfolio pf) {
		if (pf == null) {
			return null;
		}
		ObjPortfolioDto.ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder();
		this.fromAggregate(dtoBuilder, pf);
		// @formatter:off
		return dtoBuilder
			.name(pf.getName())
			.description(pf.getDescription())
			.portfolioNr(pf.getPortfolioNr())
			.accountId(pf.getAccountId() != null ? pf.getAccountId().toString() : null)
			.includes(pf.getIncludeSet().stream().map(id -> getObj((Integer)id)).collect(Collectors.toSet()))
			.excludes(pf.getExcludeSet().stream().map(id -> getObj((Integer)id)).collect(Collectors.toSet()))
			.buildings(pf.getBuildingSet().stream().map(id -> getObj((Integer)id)).collect(Collectors.toSet()))
			.build();
		// @formatter:on
	}

	private EnumeratedDto getObj(Integer id) {
		Obj obj = this.objRepository.get(id);
		return EnumeratedDto.of(obj.getId().toString(), obj.getCaption());
	}

}
