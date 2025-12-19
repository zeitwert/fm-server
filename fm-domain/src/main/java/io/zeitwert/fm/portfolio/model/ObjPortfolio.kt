package io.zeitwert.fm.portfolio.model

import io.dddrive.core.obj.model.Obj
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.task.model.ItemWithTasks

interface ObjPortfolio :
	Obj,
	ItemWithAccount,
	ItemWithNotes,
	ItemWithTasks {

	var name: String?

	var description: String?

	var portfolioNr: String?

	val buildingSet: Set<Any>

	val includeSet: Set<Any>

	fun clearIncludeSet()

	fun addInclude(id: Any)

	fun removeInclude(id: Any)

	val excludeSet: Set<Any>

	fun clearExcludeSet()

	fun addExclude(id: Any)

	fun removeExclude(id: Any)

	val inflationRate: Double

	val discountRate: Double

	fun getCondition(year: Int): Int

	fun getPortfolioValue(year: Int): Double

}
