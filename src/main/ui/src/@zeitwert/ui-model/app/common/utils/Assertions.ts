function throwError(name: string, message?: string, ...args: any[]) {
	let argIndex = 0;
	const m = message
		? message.replace(/%s/g, function (): string {
				return args[argIndex++];
		  })
		: "";
	const error = new Error(m);
	error.name = name;
	throw error;
}

export function assertThis(condition: boolean, message?: string, ...args: any[]) {
	if (condition) {
		return;
	}
	throwError("Assertion violated", message, ...args);
}

export function requireThis(condition: boolean, message?: string, ...args: any[]) {
	if (condition) {
		return;
	}
	throwError("Precondition violated", message, ...args);
}

export function ensureThis(condition: boolean, message?: string, ...args: any[]) {
	if (condition) {
		return;
	}
	throwError("Postcondition violated", message, ...args);
}
