import { Currency } from "../Currency";
import { FormatterImpl } from "../impl/FormatterImpl";
import { Language } from "../Language";
import { Locale } from "../Locale";

it("gives back current locale/language", () => {
	const f1 = new FormatterImpl(Locale.en_us);

	expect(f1.currentLocale()).toBe(Locale.en_us);
	expect(f1.currentLanguage()).toBe(Language.en);

	const f2 = new FormatterImpl(Language.en);

	expect(f2.currentLocale()).toBe(Language.en);
	expect(f2.currentLanguage()).toBe(Language.en);
});

it("formats values by language", () => {
	const f = new FormatterImpl(Language.en);

	expect(f.formatValue(100, 2)).toBe("100.00");
	expect(f.formatValue(100, 0)).toBe("100");
	expect(f.formatValue(100.01, 0)).toBe("100");
	expect(f.formatValue(1000, 2)).toBe("1,000.00");

	expect(f.formatValue(100, 2, Language.en)).toBe("100.00");
	expect(f.formatValue(100, 0, Language.en)).toBe("100");
	expect(f.formatValue(100.01, 0, Language.en)).toBe("100");
	expect(f.formatValue(1000, 2, Language.en)).toBe("1,000.00");

	// cannot test other locales, since node.js does not include them
	// expect(f.formatValue(100, 2, Locale.de_ch)).toBe("100.00");
	// expect(f.formatValue(100, 0, Locale.de_ch)).toBe("100");
	// expect(f.formatValue(1000, 2, Locale.de_ch)).toBe("1'000.00");
});

it("formats values by locale", () => {
	const f = new FormatterImpl(Locale.en_us);

	expect(f.formatValue(100, 2)).toBe("100.00");
	expect(f.formatValue(100, 0)).toBe("100");
	expect(f.formatValue(100.01, 0)).toBe("100");
	expect(f.formatValue(1000, 2)).toBe("1,000.00");

	expect(f.formatValue(100, 2, Locale.en_us)).toBe("100.00");
	expect(f.formatValue(100, 0, Locale.en_us)).toBe("100");
	expect(f.formatValue(100.01, 0, Locale.en_us)).toBe("100");
	expect(f.formatValue(1000, 2, Locale.en_us)).toBe("1,000.00");

	// cannot test other locales, since node.js does not include them
	// expect(f.formatValue(100, 2, Locale.de_ch)).toBe("100.00");
	// expect(f.formatValue(100, 0, Locale.de_ch)).toBe("100");
	// expect(f.formatValue(1000, 2, Locale.de_ch)).toBe("1'000.00");
});

it("formats percentages", () => {
	const f = new FormatterImpl(Locale.en_us);

	expect(f.formatPercent(1, 2)).toBe("100.00%");
	expect(f.formatPercent(1, 0)).toBe("100%");
	expect(f.formatPercent(1.01, 0)).toBe("101%");
	expect(f.formatPercent(10, 2)).toBe("1,000.00%");

	expect(f.formatPercent(1, 2, Locale.en_us)).toBe("100.00%");
	expect(f.formatPercent(1, 0, Locale.en_us)).toBe("100%");
	expect(f.formatPercent(1.01, 0, Locale.en_us)).toBe("101%");
	expect(f.formatPercent(10, 2, Locale.en_us)).toBe("1,000.00%");
});

it("formats amounts", () => {
	const f = new FormatterImpl(Locale.en_us);
	const NBSP = String.fromCharCode(160);

	expect(f.formatAmount(100, Currency.CHF, 2)).toBe("CHF" + NBSP + "100.00");
	expect(f.formatAmount(100, Currency.CHF, 0)).toBe("CHF" + NBSP + "100");
	expect(f.formatAmount(100.01, Currency.CHF, 0)).toBe("CHF" + NBSP + "100");
	expect(f.formatAmount(1000, Currency.CHF, 2)).toBe("CHF" + NBSP + "1,000.00");

	expect(f.formatAmount(-100, Currency.CHF, 2)).toBe("CHF" + NBSP + "-100.00");
	expect(f.formatAmount(-100, Currency.CHF, 0)).toBe("CHF" + NBSP + "-100");
	expect(f.formatAmount(-100.01, Currency.CHF, 0)).toBe("CHF" + NBSP + "-100");
	expect(f.formatAmount(-1000, Currency.CHF, 2)).toBe("CHF" + NBSP + "-1,000.00");

	expect(f.formatAmount(100, Currency.USD, 2, Locale.en_us)).toBe("USD" + NBSP + "100.00");
	expect(f.formatAmount(100, Currency.USD, 0, Locale.en_us)).toBe("USD" + NBSP + "100");
	expect(f.formatAmount(100.01, Currency.USD, 0, Locale.en_us)).toBe("USD" + NBSP + "100");
	expect(f.formatAmount(1000, Currency.USD, 2, Locale.en_us)).toBe("USD" + NBSP + "1,000.00");

});
