export enum Language {
	en = "en",
	de = "de",
	fr = "fr",
	es = "es"
}

export const Languages = [Language.en, Language.de, Language.es];

export function isLanguage(language: string) {
	return Languages.indexOf(language as Language) >= 0;
}
