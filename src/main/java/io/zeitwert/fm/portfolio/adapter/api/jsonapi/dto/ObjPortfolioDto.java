package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios")
public class ObjPortfolioDto extends FMObjDtoBase<ObjPortfolio> {

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
				this.accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(this.getOriginal().getAccount());
			} else if (this.accountId != null) {
			}
		}
		return this.accountDto;
	}

	// Crnk needs to see this to set accountId
	public void setAccount(ObjAccountDto account) {
	}

}
