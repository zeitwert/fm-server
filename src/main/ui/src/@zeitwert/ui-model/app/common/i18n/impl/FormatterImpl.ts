import { Locale, language } from "../Locale";
import { Language } from "../Language";
import { Currency } from "../Currency";
import { Formatter } from "../Formatter";

export class FormatterImpl implements Formatter {
	locale: Locale | Language;

	constructor(locale: Locale | Language) {
		this.locale = locale;
	}

	currentLocale(): Locale | Language {
		return this.locale;
	}

	currentLanguage(): Language {
		return language(this.currentLocale());
	}

	valueFormatter(locale: Locale | Language, digits: number): Intl.NumberFormat {
		return new Intl.NumberFormat(locale, {
			minimumFractionDigits: digits,
			maximumFractionDigits: digits
		});
	}

	formatValue(value: number, digits: number = 2, locale?: Locale | Language): string {
		return this.valueFormatter(locale || this.currentLocale(), digits).format(value);
	}

	percentFormatter(locale: Locale | Language, digits: number): Intl.NumberFormat {
		return new Intl.NumberFormat(locale, {
			style: "percent",
			minimumFractionDigits: digits,
			maximumFractionDigits: digits
		});
	}

	formatPercent(value: number, digits: number = 1, locale?: Locale | Language): string {
		return this.percentFormatter(locale || this.currentLocale(), digits).format(value);
	}

	amountFormatter(locale: Locale | Language, currency: Currency, digits: number): Intl.NumberFormat {
		return new Intl.NumberFormat(locale, {
			style: "currency",
			currency: currency,
			currencyDisplay: "code",
			minimumFractionDigits: digits,
			maximumFractionDigits: digits
		});
	}

	formatAmount(amount: number, currency: Currency, digits: number = 2, locale?: Locale | Language): string {
		return this.amountFormatter(locale || this.currentLocale(), currency, digits)
			.format(amount)
			.replace(/^(\D+)/, "$1 ");
	}
}
