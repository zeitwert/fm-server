
import { USER_PREF_ITEM } from "../../common";

export class UserPrefs {

	getUserPref(entityType: string, pref: string): any {
		const prefs = JSON.parse(localStorage.getItem(USER_PREF_ITEM) || "{}");
		const entityPrefs = prefs[entityType];
		return entityPrefs?.[pref];
	}

	setUserPref(entityType: string, pref: string, value: any): void {
		const prefs = JSON.parse(localStorage.getItem(USER_PREF_ITEM) || "{}");
		const entityPrefs = prefs[entityType] || {};
		entityPrefs[pref] = value;
		prefs[entityType] = entityPrefs;
		localStorage.setItem(USER_PREF_ITEM, JSON.stringify(prefs));
	}

}

const userPrefs = new UserPrefs();

export default userPrefs;
