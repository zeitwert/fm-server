package io.zeitwert.fm.portfolio.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.dddrive.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "portfolio", resourcePath = "portfolio/portfolios")
class ObjPortfolioDto : ObjDtoBase()
