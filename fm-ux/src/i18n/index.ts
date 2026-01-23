import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import ICU from "i18next-icu";

// Import translation files
import enCommon from "./locales/en/common.json";
import enLogin from "./locales/en/login.json";
import enApp from "./locales/en/app.json";
import enHome from "./locales/en/home.json";
import enAccount from "./locales/en/account.json";
import enContact from "./locales/en/contact.json";
import enNote from "./locales/en/note.json";
import enTask from "./locales/en/task.json";
import enTenant from "./locales/en/tenant.json";
import enUser from "./locales/en/user.json";
import enPortfolio from "./locales/en/portfolio.json";

import deCommon from "./locales/de/common.json";
import deLogin from "./locales/de/login.json";
import deApp from "./locales/de/app.json";
import deHome from "./locales/de/home.json";
import deAccount from "./locales/de/account.json";
import deContact from "./locales/de/contact.json";
import deNote from "./locales/de/note.json";
import deTask from "./locales/de/task.json";
import deTenant from "./locales/de/tenant.json";
import deUser from "./locales/de/user.json";
import dePortfolio from "./locales/de/portfolio.json";

// Each translation file is a separate namespace
// This allows using t("account:label.name") with namespace:key syntax
const resources = {
	en: {
		common: enCommon,
		login: enLogin,
		app: enApp,
		home: enHome,
		account: enAccount,
		contact: enContact,
		note: enNote,
		task: enTask,
		tenant: enTenant,
		user: enUser,
		portfolio: enPortfolio,
	},
	de: {
		common: deCommon,
		login: deLogin,
		app: deApp,
		home: deHome,
		account: deAccount,
		contact: deContact,
		note: deNote,
		task: deTask,
		tenant: deTenant,
		user: deUser,
		portfolio: dePortfolio,
	},
};

const namespaces = [
	"common",
	"login",
	"app",
	"home",
	"account",
	"contact",
	"note",
	"task",
	"tenant",
	"user",
	"portfolio",
];

i18n
	.use(ICU)
	.use(initReactI18next)
	.init({
		resources,
		lng: "de", // Default language before login
		fallbackLng: "de",
		ns: namespaces,
		defaultNS: "common", // Keys without namespace prefix resolve to common
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
