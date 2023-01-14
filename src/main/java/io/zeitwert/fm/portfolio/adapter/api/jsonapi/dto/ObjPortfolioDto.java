package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios")
public class ObjPortfolioDto extends FMObjDtoBase<ObjPortfolio> {

	@JsonApiRelationId
	private Integer accountId;

	@JsonIgnore
	private ObjAccountDto accountDto;

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
		this.accountDto = null;
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjAccountDto getAccount() {
		if (this.accountDto == null) {
			ObjAccount account = null;
			if (this.getOriginal() != null) {
				account = this.getOriginal().getAccount();
			} else if (this.accountId != null) {
				account = getService(ObjAccountCache.class).get(this.accountId);
			}
			this.accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account);
		}
		return this.accountDto;
	}

	public void setAccount(ObjAccountDto account) {
		this.accountDto = account;
		this.accountId = account != null ? account.getId() : null;
	}

	private String name;
	private String description;
	private String portfolioNr;
	private Set<EnumeratedDto> includes;
	private Set<EnumeratedDto> excludes;
	private Set<EnumeratedDto> buildings;

}
