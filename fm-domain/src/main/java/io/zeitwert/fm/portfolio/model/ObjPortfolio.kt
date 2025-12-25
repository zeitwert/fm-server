package io.zeitwert.fm.portfolio.model

import io.dddrive.obj.model.Obj
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.building.model.ObjBuilding
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

	val buildingSet: ReferenceSetProperty<ObjBuilding>

	val includeSet: ReferenceSetProperty<Obj>

	val excludeSet: ReferenceSetProperty<Obj>

	val inflationRate: Double

	val discountRate: Double

	fun getCondition(year: Int): Int

	fun getPortfolioValue(year: Int): Double
}
