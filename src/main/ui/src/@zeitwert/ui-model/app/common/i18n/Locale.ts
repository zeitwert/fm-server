import * as Language from "./Language";

export enum Locale {
	en_us = "en-US",
	en_gb = "en-GB",
	de_ch = "de-CH",
	de_de = "de-DE",
	fr_ch = "fr-CH",
	fr_fr = "fr-FR",
	es_es = "es-ES"
}

export const Locales = [
	Locale.en_us,
	Locale.en_gb,
	Locale.de_ch,
	Locale.de_de,
	Locale.fr_ch,
	Locale.fr_fr,
	Locale.es_es
];

export function isLocale(locale: string) {
	return Locales.indexOf(locale as Locale) >= 0;
}

export function language(locale: string): Language.Language {
	return locale.substr(0, 2) as Language.Language;
}
