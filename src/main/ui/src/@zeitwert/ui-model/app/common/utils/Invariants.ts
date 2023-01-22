
function throwError(name: string, message?: string | (() => string), ...args: any[]) {
	let argIndex = 0;
	if (typeof message === "function") {
		message = message();
	}
	const m = message
		? message.replace(/%s/g, function (): string { return args[argIndex++]; })
		: "";
	const error = new Error(m ?? name);
	error.name = name;
	throw error;
}

export function assertThis(condition: boolean, message?: string | (() => string), ...args: any[]): asserts condition {
	if (condition) {
		return;
	}
	throwError("Assertion failed", message, ...args);
}

export function requireThis(condition: boolean, message?: string | (() => string), ...args: any[]): asserts condition {
	if (condition) {
		return;
	}
	throwError("Precondition failed", message, ...args);
}

export function ensureThis(condition: boolean, message?: string | (() => string), ...args: any[]): asserts condition {
	if (condition) {
		return;
	}
	throwError("Postcondition failed", message, ...args);
}
