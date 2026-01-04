package io.zeitwert.fm.portfolio.model

import dddrive.app.obj.model.Obj
import dddrive.ddd.property.model.AggregateReferenceSetProperty
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

	val buildingSet: AggregateReferenceSetProperty<ObjBuilding>

	val includeSet: AggregateReferenceSetProperty<Obj>

	val excludeSet: AggregateReferenceSetProperty<Obj>

	val inflationRate: Double

	val discountRate: Double

	fun getCondition(year: Int): Int

	fun getPortfolioValue(year: Int): Double
}
