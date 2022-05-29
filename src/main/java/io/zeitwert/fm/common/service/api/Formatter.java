package io.zeitwert.fm.common.service.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {

	public static final Formatter INSTANCE = new Formatter();

	private final DecimalFormat formatter;

	public Formatter() {
		this.formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('\'');
		formatter.setDecimalFormatSymbols(symbols);
	}

	public String formatNumber(Object value) {
		return this.formatter.format(value);
	}

	public String formatMonetaryValue(Object value, String currency) {
		return formatter.format(value) + " " + currency;
	}

	public String formatValueWithUnit(Object value, String unit) {
		return formatter.format(value) + " " + unit;
	}

}
