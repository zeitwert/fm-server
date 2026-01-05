package io.zeitwert.fm.app.adapter.api.rest.dto

data class HomeOverviewResponse(
	val accountId: Int,
	val accountName: String,
	val buildingCount: Int,
	val portfolioCount: Int,
	val ratingCount: Int,
	val insuranceValue: Int,
	val timeValue: Int,
	val shortTermRenovationCosts: Int,
	val midTermRenovationCosts: Int,
)
