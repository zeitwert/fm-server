package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component("objPortfolioDtoAdapter")
public class ObjPortfolioDtoAdapter
		extends ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioDto> {

	private ObjAccountRepository accountRepository = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

// private final ObjVCache objCache;

//	protected ObjPortfolioDtoAdapter(ObjVCache objCache) {
//		this.objCache = objCache;
//	}

	@Autowired
	void setAccountRepository(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjPortfolioDto dto, ObjPortfolio pf) {
		try {
			pf.getMeta().disableCalc();
			super.toAggregate(dto, pf);

			pf.name = dto.getName();
			pf.description = dto.getDescription();
			pf.portfolioNr = dto.getPortfolioNr();
			pf.accountId = dto.getAccountId();
			// TODO prevent calculation during insert
			if (dto.getIncludes() != null) {
				pf.clearIncludeSet();
				dto.getIncludes().forEach(item -> {
					Integer id = Integer.parseInt(item.getId());
					pf.addInclude(id);
				});
			}
			if (dto.getExcludes() != null) {
				pf.clearExcludeSet();
				dto.getExcludes().forEach(item -> {
					Integer id = Integer.parseInt(item.getId());
					pf.addExclude(id);
				});
			}

		} finally {
			pf.getMeta().enableCalc();
			pf.calcAll();
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
			.name(pf.name)
			.description(pf.description)
			.portfolioNr(pf.portfolioNr)
			.accountId((Integer)pf.accountId)
			.includes(pf.includeSet.stream().map(this::getObj).collect(Collectors.toSet()))
			.excludes(pf.excludeSet.stream().map(this::getObj).collect(Collectors.toSet()))
			.buildings(pf.buildingSet.stream().map(this::getObj).collect(Collectors.toSet()))
			.build();
		// @formatter:on
	}

//	@Override
//	public ObjPortfolioDto fromRecord(ObjPortfolioVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjPortfolioDto.ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		// @formatter:off
//		return dtoBuilder
//			.name(obj.getName())
//			.description(obj.getDescription())
//			.portfolioNr(obj.getPortfolioNr())
//			.accountId(obj.getAccountId())
//			.build();
//		// @formatter:on
//	}

	private EnumeratedDto getObj(Integer id) {
		return EnumeratedDto.of(id.toString(), id.toString());
//		Obj obj = this.objCache.get(id);
//		return EnumeratedDto.of(obj.getId().toString(), obj.getCaption());
		// @formatter:off
//		return EnumeratedDto.builder()
//			.id(obj.getId().toString())
//			.name(obj.getCaption())
//			.itemType(EnumeratedDto.of(obj.getMeta().getAggregateType()))
//			.build();
		// @formatter:on
	}

}
