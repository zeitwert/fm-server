package io.zeitwert.fm.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Formatter {

	private val decimalFormatter: DecimalFormat
	private val dateFormatter: DateTimeFormatter
	private val isoDateFormatter: DateTimeFormatter

	init {
		this.decimalFormatter = NumberFormat.getInstance(Locale.GERMAN) as DecimalFormat
		val symbols = this.decimalFormatter.decimalFormatSymbols
		symbols.setGroupingSeparator('\'')
		this.decimalFormatter.decimalFormatSymbols = symbols
		this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
		this.isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	}

	fun formatNumber(value: Any?): String = this.decimalFormatter.format(value)

	fun formatMonetaryValue(
		value: Any?,
		currency: String?,
	): String = this.decimalFormatter.format(value) + " " + currency

	fun formatValueWithUnit(
		value: Any?,
		unit: String?,
	): String = this.decimalFormatter.format(value) + " " + unit

	fun formatDate(value: LocalDate): String = this.dateFormatter.format(value)

	fun formatIsoDate(value: LocalDate): String = this.isoDateFormatter.format(value)

}
