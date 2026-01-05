package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import org.springframework.stereotype.Component

@Component("objPortfolioDtoAdapter")
class ObjPortfolioDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioDto>(directory, { ObjPortfolioDto() }) {

	init {
		// relationship("accountId", "account", "accountId")
		field("includes", "includeSet")
		field("excludes", "excludeSet")
		field("buildings", "buildingSet")
	}

}
