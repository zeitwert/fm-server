
package io.dddrive.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatter {

	public static final Formatter INSTANCE = new Formatter();

	private final DecimalFormat decimalFormatter;
	private final DateTimeFormatter dateFormatter;
	private final DateTimeFormatter isoDateFormatter;

	public Formatter() {
		this.decimalFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
		DecimalFormatSymbols symbols = this.decimalFormatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('\'');
		this.decimalFormatter.setDecimalFormatSymbols(symbols);
		this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		this.isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	public String formatNumber(Object value) {
		return this.decimalFormatter.format(value);
	}

	public String formatMonetaryValue(Object value, String currency) {
		return this.decimalFormatter.format(value) + " " + currency;
	}

	public String formatValueWithUnit(Object value, String unit) {
		return this.decimalFormatter.format(value) + " " + unit;
	}

	public String formatDate(LocalDate value) {
		return this.dateFormatter.format(value);
	}

	public String formatIsoDate(LocalDate value) {
		return this.isoDateFormatter.format(value);
	}

}
