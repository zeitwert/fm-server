import { Language } from "../Language";
import { Locale, language } from "../Locale";
import { TranslatorImpl } from "../impl/TranslatorImpl";

it("gives back current locale/language", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.currentLocale()).toBe(Locale.en_us);
	expect(t.currentLanguage()).toBe(Language.en);

	expect(language(Locale.en_us)).toBe(Language.en);
	expect(language(Locale.de_ch)).toBe(Language.de);
});

it("ignores values", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.translate("a simple text", Locale.en_us)).toBe("a simple text");
});

it("translates language keys", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.translate("@test.label.yes", Language.en)).toBe("Yes");
	expect(t.translate("@test.label.null", Language.en)).toBeNull();
	expect(t.translate("@test.view:demo.yes", Language.en)).toBe("Yesss");
});

it("translates locale keys", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.translate("@test.label.yes", Locale.en_us)).toBe("Yes");
	expect(t.translate("@test.label.null", Locale.en_us)).toBeNull();
	expect(t.translate("@test.view:demo.yes", Locale.en_us)).toBe("Si");
});

it("recursively translates keys", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.translate("@test.action.yes", Locale.en_us)).toBe("Yes");
	expect(t.translate("@test.action.no", Locale.en_us)).toBe("No");
	expect(t.translate("@test.view:demo.no", Locale.en_us)).toBe("No");

	expect(t.translate("@test.action.yes", Locale.de_ch)).toBe("Ja");
	expect(t.translate("@test.action.no", Locale.de_ch)).toBe("No");
});

it("returns missing keys", () => {
	const t = new TranslatorImpl(Locale.en_us);

	expect(t.translate("@test.missing", Locale.en_us)).toBe("[" + Locale.en_us + "] @test.missing");
});
