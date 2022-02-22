const env = process.env;

export const Env = {
	getParam(param: string): string {
		return env["REACT_APP_" + param]!;
	}
};
