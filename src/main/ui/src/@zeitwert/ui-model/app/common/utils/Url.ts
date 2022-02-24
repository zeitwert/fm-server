// incomplete copy from https://github.com/sindresorhus/query-string
// does not support multi-values
export function parseUrl(str: string): any {
	// Create an object with no prototype
	// https://github.com/sindresorhus/query-string/issues/47
	var ret = Object.create(null);

	if (typeof str !== "string") {
		return ret;
	}

	str = str.trim().replace(/^[?#&]/, "");

	if (!str) {
		return ret;
	}

	str.split("&").forEach(function (param: string) {
		var parts = param.replace(/\+/g, " ").split("=");
		// Firefox (pre 40) decodes `%3D` to `=`
		// https://github.com/sindresorhus/query-string/pull/37
		var key = parts.shift();
		var val = parts.length > 0 ? parts.join("=") : null;

		// missing `=` should be `null`:
		// http://w3.org/TR/2012/WD-url-20120524/#collect-url-parameters
		val = val === undefined ? null : decodeURIComponent(val!);
		ret[decodeURIComponent(key!)] = val;

		// formatter(decodeURIComponent(key), val, ret);
	});

	return Object.keys(ret)
		.sort()
		.reduce(function (result: any, key: string) {
			var val = ret[key];
			if (Boolean(val) && typeof val === "object" && !Array.isArray(val)) {
				// Sort object keys, not values
				result[key] = keysSorter(val);
			} else {
				result[key] = val;
			}
			return result;
		}, Object.create(null));
}

function keysSorter(input: any): any {
	if (Array.isArray(input)) {
		return input.sort();
	} else if (typeof input === "object") {
		return keysSorter(Object.keys(input))
			.sort(function (a: string, b: string): number {
				return Number(a) - Number(b);
			})
			.map(function (key: string): string {
				return input[key];
			});
	}

	return input;
}
