package io.zeitwert.fm.portfolio.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.portfolio.adapter.jsonapi.dto.ObjPortfolioDto
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import org.springframework.stereotype.Component

@Component("objPortfolioDtoAdapter")
class ObjPortfolioDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjPortfolio, ObjPortfolioDto>(
		ObjPortfolio::class.java,
		"portfolio",
		ObjPortfolioDto::class.java,
		directory,
		{ ObjPortfolioDto() },
	) {

	init {
		config.field("includes", "includeSet")
		config.field("excludes", "excludeSet")
		config.field("buildings", "buildingSet")
	}

}
