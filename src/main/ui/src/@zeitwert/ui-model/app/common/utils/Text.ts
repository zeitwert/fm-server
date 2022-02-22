import { requireThis } from "./Assertions";

export function replaceValues(text: string, values: any) {
	requireThis(!!text, "valid text");
	requireThis(!!values, "valid values");
	/* eslint-disable no-new-func */
	const fn = new Function("data", "return `" + text + "`");
	try {
		return fn(values);
	} catch (ex) {
		throw new Error(
			`Template value replacement failed\nTemplate ${text}\nValues: ` + JSON.stringify(values, null, 2)
		);
	}
}

export function stripHtml(html: string) {
	if (html) {
		let div = document.createElement("div");
		div.innerHTML = html;
		return div.textContent || div.innerText || "";
	}
	return html;
}

export function decodeHtml(input: string) {
	const doc = new DOMParser().parseFromString(input, "text/html");
	return doc.documentElement.textContent || "";
}
