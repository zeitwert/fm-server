import { requireThis } from "./Assertions";

export function setCookie(name: string, value: string, days: number | null = null) {
	requireThis(name.indexOf(";") < 0, "cookie name does not contain semicolon");
	var expiry = "";
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
		expiry = "; expires=" + date.toUTCString();
	}
	document.cookie = name + "=" + value + expiry + "; path=/";
}

export function getCookie(name: string): string | null {
	var nameEq = name + "=";
	var ca = document.cookie.split(";");
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) === " ") {
			c = c.substring(1, c.length);
		}
		if (c.indexOf(nameEq) === 0) {
			return c.substring(nameEq.length, c.length);
		}
	}
	return null;
}

export function clearCookie(name: string) {
	setCookie(name, "", -1);
}
