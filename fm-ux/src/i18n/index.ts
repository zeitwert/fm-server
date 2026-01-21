import i18n from "i18next";
import { initReactI18next } from "react-i18next";

// Import translation files
import enCommon from "./locales/en/common.json";
import enLogin from "./locales/en/login.json";
import enApp from "./locales/en/app.json";
import enHome from "./locales/en/home.json";
import enAccount from "./locales/en/account.json";
import enContact from "./locales/en/contact.json";

import deCommon from "./locales/de/common.json";
import deLogin from "./locales/de/login.json";
import deApp from "./locales/de/app.json";
import deHome from "./locales/de/home.json";
import deAccount from "./locales/de/account.json";
import deContact from "./locales/de/contact.json";

// Resources bundled inline for synchronous loading
const resources = {
	en: {
		common: enCommon,
		login: enLogin,
		app: enApp,
		home: enHome,
		account: enAccount,
		contact: enContact,
	},
	de: {
		common: deCommon,
		login: deLogin,
		app: deApp,
		home: deHome,
		account: deAccount,
		contact: deContact,
	},
};

i18n.use(initReactI18next).init({
	resources,
	lng: "de", // Default language before login
	fallbackLng: "de",
	ns: ["common", "login", "app", "home", "account", "contact"],
	defaultNS: "common",
	interpolation: {
		escapeValue: false, // React already escapes values
	},
});

/**
 * Sync the i18n language with the session locale.
 * Call this after login/session restore when sessionInfo.locale is available.
 */
export function changeLanguage(locale: string | undefined) {
	if (locale && i18n.language !== locale) {
		i18n.changeLanguage(locale);
	}
}

export default i18n;
