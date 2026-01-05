package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase
import io.zeitwert.fm.portfolio.model.ObjPortfolio

@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios")
class ObjPortfolioDto : ObjDtoBase<ObjPortfolio>() {

	@JsonApiRelationId
	var accountId: String? = null
		get() = getRelation("accountId") as String?
		set(value) {
			setRelation("accountId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var account: ObjAccountDto? = null

}
