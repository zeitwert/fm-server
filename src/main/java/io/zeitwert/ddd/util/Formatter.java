
package io.zeitwert.ddd.util;

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

	public Formatter() {
		this.decimalFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
		DecimalFormatSymbols symbols = decimalFormatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('\'');
		decimalFormatter.setDecimalFormatSymbols(symbols);
		this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	}

	public String formatNumber(Object value) {
		return this.decimalFormatter.format(value);
	}

	public String formatMonetaryValue(Object value, String currency) {
		return decimalFormatter.format(value) + " " + currency;
	}

	public String formatValueWithUnit(Object value, String unit) {
		return decimalFormatter.format(value) + " " + unit;
	}

	public String formatDate(LocalDate value) {
		return this.dateFormatter.format(value);
	}

}
