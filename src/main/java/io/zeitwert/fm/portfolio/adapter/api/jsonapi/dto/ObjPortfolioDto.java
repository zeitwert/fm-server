package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static io.zeitwert.ddd.util.Check.assertThis;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios", deletable = false)
public class ObjPortfolioDto extends FMObjDtoBase<ObjPortfolio> {

	private static final ObjVRepository objRepository = (ObjVRepository) AppContext.getInstance()
			.getRepository(Obj.class);

	private static final List<CodeAggregateType> OBJ_TYPES = List.of(
			CodeAggregateTypeEnum.getAggregateType("obj_portfolio"),
			CodeAggregateTypeEnum.getAggregateType("obj_account"),
			CodeAggregateTypeEnum.getAggregateType("obj_building"));

	@JsonIgnore
	private ObjAccountDto accountDto;

	private String name;
	private String description;
	private String portfolioNr;
	private Set<EnumeratedDto> includes;
	private Set<EnumeratedDto> excludes;
	private Set<EnumeratedDto> buildings;

	@JsonApiRelationId
	private Integer accountId;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			if (this.getOriginal() != null) {
				this.accountDto = ObjAccountDto.fromObj(this.getOriginal().getAccount(), this.sessionInfo);
			} else if (this.accountId != null) {
			}
		}
		return this.accountDto;
	}

	// Crnk needs to see this to set accountId
	public void setAccount(ObjAccountDto account) {
	}

	@Override
	public void toObj(ObjPortfolio pf) {
		super.toObj(pf);
		pf.setName(this.name);
		pf.setDescription(this.description);
		pf.setPortfolioNr(this.portfolioNr);
		pf.setAccountId(this.accountId);
		// TODO prevent calculation during insert
		SessionInfo sessionInfo = pf.getMeta().getSessionInfo();
		if (this.includes != null) {
			pf.clearIncludeSet();
			this.includes.forEach(item -> {
				Integer id = Integer.parseInt(item.getId());
				Obj obj = objRepository.get(sessionInfo, id);
				CodeAggregateType objType = obj.getMeta().getAggregateType();
				assertThis(OBJ_TYPES.indexOf(objType) >= 0, "supported objType " + id);
				pf.addInclude(id);
			});
		}
		if (this.excludes != null) {
			pf.clearExcludeSet();
			this.excludes.forEach(item -> {
				Integer id = Integer.parseInt(item.getId());
				Obj obj = objRepository.get(sessionInfo, id);
				CodeAggregateType objType = obj.getMeta().getAggregateType();
				assertThis(OBJ_TYPES.indexOf(objType) >= 0, "supported objType " + id);
				pf.addExclude(id);
			});
		}
	}

	public static ObjPortfolioDto fromObj(ObjPortfolio pf, SessionInfo sessionInfo) {
		if (pf == null) {
			return null;
		}
		ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder().original(pf);
		FMObjDtoBase.fromObj(dtoBuilder, pf, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(pf.getName())
			.description(pf.getDescription())
			.portfolioNr(pf.getPortfolioNr())
			.accountId(pf.getAccountId())
			.includes(pf.getIncludeSet().stream().map(a -> getObj(sessionInfo, a)).collect(Collectors.toSet()))
			.excludes(pf.getExcludeSet().stream().map(a -> getObj(sessionInfo, a)).collect(Collectors.toSet()))
			.buildings(pf.getBuildingSet().stream().map(a -> getObj(sessionInfo, a)).collect(Collectors.toSet()))
			.build();
		// @formatter:on
	}

	private static EnumeratedDto getObj(SessionInfo sessionInfo, Integer id) {
		Obj obj = objRepository.get(sessionInfo, id);
		// @formatter:off
		return EnumeratedDto.builder()
			.id(obj.getId().toString())
			.name(obj.getCaption())
			.itemType(EnumeratedDto.fromEnum(obj.getMeta().getAggregateType()))
			.build();
		// @formatter:on
	}

	public static ObjPortfolioDto fromRecord(ObjPortfolioVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjPortfolioDtoBuilder<?, ?> dtoBuilder = ObjPortfolioDto.builder().original(null);
		FMObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.description(obj.getDescription())
			.portfolioNr(obj.getPortfolioNr())
			.accountId(obj.getAccountId())
			.build();
		// @formatter:on
	}

}
