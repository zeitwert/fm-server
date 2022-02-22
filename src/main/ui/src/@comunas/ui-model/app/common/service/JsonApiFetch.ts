import { API_CONTENT_TYPE, AUTH_HEADER_ITEM, convertJsonApiToJson } from "@comunas/ui-model";
import { _ } from "@finadvise/forms";

interface FetchResponse {
	json: () => Promise<any>;
	text: () => Promise<string>;
	headers: {
		get: (key: string) => string;
	};
	body: any;
}

export function jsonApiFetch(url: string, opts?: any, onProgress?: any): Promise<FetchResponse> {

	let headers: Headers = opts.headers;
	if (!headers) {
		headers = new Headers();
	}
	headers.set("Authorization", sessionStorage.getItem(AUTH_HEADER_ITEM)!);
	opts = Object.assign(opts, {
		headers: headers
	});

	return _.futch(url, opts, onProgress).then(async (rsp: FetchResponse) => {
		const contentType = ";" + rsp.headers.get("Content-Type").trim() + ";";
		if (contentType.indexOf(";" + API_CONTENT_TYPE + ";") >= 0) {
			const body = await rsp.json();
			const data = await convertJsonApiToJson(body);
			rsp = Object.assign({}, rsp, {
				json: () => Promise.resolve(data),
				body: convertJsonApiToJson(rsp.body)
			});
		}
		return rsp;
	});

}
