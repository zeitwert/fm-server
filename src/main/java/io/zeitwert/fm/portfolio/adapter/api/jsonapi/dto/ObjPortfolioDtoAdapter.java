
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

@Component("objPortfolioDtoAdapter")
public class ObjPortfolioDtoAdapter
		extends ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioVRecord, ObjPortfolioDto> {

	private ObjAccountCache accountCache = null;
	private ObjAccountDtoAdapter accountDtoAdapter;

	private final ObjVCache objCache;

	protected ObjPortfolioDtoAdapter(ObjVCache objCache) {
		this.objCache = objCache;
	}

	@Autowired
	void setAccountCache(ObjAccountCache accountCache) {
		this.accountCache = accountCache;
	}

	@Autowired
	void setAccountDtoAdapter(ObjAccountDtoAdapter accountDtoAdapter) {
		this.accountDtoAdapter = accountDtoAdapter;
	}

	public ObjAccountDto getAccountDto(Integer id) {
		return id != null ? this.accountDtoAdapter.fromAggregate(this.accountCache.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjPortfolioDto dto, ObjPortfolio pf) {
		try {
			pf.getMeta().disableCalc();
			super.toAggregate(dto, pf);

			pf.setName(dto.getName());
			pf.setDescription(dto.getDescription());
			pf.setPortfolioNr(dto.getPortfolioNr());
			pf.setAccountId(dto.getAccountId());
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
			.name(pf.getName())
			.description(pf.getDescription())
			.portfolioNr(pf.getPortfolioNr())
			.accountId(pf.getAccountId())
			.includes(pf.getIncludeSet().stream().map(a -> getObj(a)).collect(Collectors.toSet()))
			.excludes(pf.getExcludeSet().stream().map(a -> getObj(a)).collect(Collectors.toSet()))
			.buildings(pf.getBuildingSet().stream().map(a -> getObj(a)).collect(Collectors.toSet()))
			.build();
		// @formatter:on
	}

	@Override
	public ObjPortfolioDto fromRecord(ObjPortfolioVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjPortfolioDto.ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder();
		this.fromRecord(dtoBuilder, obj);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.description(obj.getDescription())
			.portfolioNr(obj.getPortfolioNr())
			.accountId(obj.getAccountId())
			.build();
		// @formatter:on
	}

	private EnumeratedDto getObj(Integer id) {
		Obj obj = this.objCache.get(id);
		// @formatter:off
		return EnumeratedDto.builder()
			.id(obj.getId().toString())
			.name(obj.getCaption())
			.itemType(EnumeratedDto.fromEnum(obj.getMeta().getAggregateType()))
			.build();
		// @formatter:on
	}

}
