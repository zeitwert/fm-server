const DEFAULT_FORMAT = {
	thousand: ".",
	decimal: ","
};

function reverse(value: any) {
	return value.split("").reverse().join("");
}

function getRegExp(symbol: any) {
	let sym = symbol;
	switch (symbol) {
		case ".":
			sym = "\\.";
			break;
		case " ":
			sym = "\\s";
	}
	return sym;
}

interface Format {
	thousand: string;
	decimal: string;
}

/**
 * Basic number formatter.
 * This code has been copied from ui-forms.
 */
export const NumberFormat = {
	formatNumber: (value: any, format: Format = DEFAULT_FORMAT) => {
		if (!value && 0 !== value) {
			return "";
		}
		let sign = "";
		let val = value.toString();
		if (val[0] === "-") {
			sign = "-";
			val = val.substring(1);
		}
		const numSplitted = val.split(".");
		const rgxSep = new RegExp("(.+)" + getRegExp(format.thousand) + "$");
		const intFormatted = reverse(
			reverse(numSplitted[0])
				.replace(/(\d{3})/g, "$1" + format.thousand)
				.replace(rgxSep, "$1")
		);
		const floated = numSplitted[1];
		return sign + intFormatted.concat(undefined !== floated ? format.decimal + floated : "");
	}
};
