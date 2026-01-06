package io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase
import io.zeitwert.fm.portfolio.model.ObjPortfolio

@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios")
class ObjPortfolioDto : ObjDtoBase<ObjPortfolio>()
