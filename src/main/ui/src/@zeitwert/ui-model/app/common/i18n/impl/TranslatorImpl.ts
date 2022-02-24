import { requireThis, valueByPath } from "../..";
import { Language } from "../Language";
import { isLocale, language, Locale } from "../Locale";
import { Translations } from "../translations";
import { Translator } from "../Translator";

export class TranslatorImpl implements Translator {
	locale: Locale;

	constructor(locale: Locale) {
		this.locale = locale;
	}

	currentLocale(): Locale {
		return this.locale;
	}

	currentLanguage(): Language {
		return language(this.currentLocale());
	}

	translate(key: string, locale: Language | Locale = this.currentLocale()): string {
		requireThis(!!key, "valid key");
		requireThis(!!locale, "valid locale");

		// return non-key
		if (!key.startsWith("@")) {
			return key;
		}

		// get translation for locale
		let langKey = locale;
		let i18nKey = langKey + "." + key.substr(1);
		let text = valueByPath(Translations, i18nKey);

		// fallback to locale.language
		if (typeof text === "undefined" && isLocale(langKey)) {
			langKey = language(langKey);
			i18nKey = langKey + "." + key.substr(1);
			text = valueByPath(Translations, i18nKey);
		}

		// fallback to english
		if (typeof text === "undefined" && langKey !== Language.en) {
			i18nKey = Language.en + "." + key.substr(1);
			text = valueByPath(Translations, i18nKey);
		}

		// recurse if necessary
		if (!!text && text.startsWith("@")) {
			text = this.translate(text, locale);
		}

		// error if undefined (but not if [null])
		if (typeof text === "undefined") {
			text = "[" + locale + "] " + key;
		}

		return text;
	}
}
