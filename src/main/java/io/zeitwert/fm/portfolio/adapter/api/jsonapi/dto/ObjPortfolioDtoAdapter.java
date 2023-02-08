
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.List;
import java.util.stream.Collectors;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

public class ObjPortfolioDtoAdapter
		extends ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioVRecord, ObjPortfolioDto> {

	private static final ObjVRepository objRepository = (ObjVRepository) AppContext.getInstance()
			.getRepository(Obj.class);

	private static final List<CodeAggregateType> OBJ_TYPES = List.of(
			CodeAggregateTypeEnum.getAggregateType("obj_portfolio"),
			CodeAggregateTypeEnum.getAggregateType("obj_account"),
			CodeAggregateTypeEnum.getAggregateType("obj_building"));

	private static ObjPortfolioDtoAdapter instance;

	private ObjPortfolioDtoAdapter() {
	}

	public static final ObjPortfolioDtoAdapter getInstance() {
		if (instance == null) {
			instance = new ObjPortfolioDtoAdapter();
		}
		return instance;
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
					Obj obj = objRepository.get(id);
					CodeAggregateType objType = obj.getMeta().getAggregateType();
					assertThis(OBJ_TYPES.indexOf(objType) >= 0, "supported objType " + id);
					pf.addInclude(id);
				});
			}
			if (dto.getExcludes() != null) {
				pf.clearExcludeSet();
				dto.getExcludes().forEach(item -> {
					Integer id = Integer.parseInt(item.getId());
					Obj obj = objRepository.get(id);
					CodeAggregateType objType = obj.getMeta().getAggregateType();
					assertThis(OBJ_TYPES.indexOf(objType) >= 0, "supported objType " + id);
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
		ObjPortfolioDto.ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder().original(pf);
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
		ObjPortfolioDto.ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder().original(null);
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

	private static EnumeratedDto getObj(Integer id) {
		Obj obj = objRepository.get(id);
		// @formatter:off
		return EnumeratedDto.builder()
			.id(obj.getId().toString())
			.name(obj.getCaption())
			.itemType(EnumeratedDto.fromEnum(obj.getMeta().getAggregateType()))
			.build();
		// @formatter:on
	}

}
