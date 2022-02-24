import { requireThis } from "../../../common/utils/Assertions";
import { Datamart, Parameter } from "../Datamart";
import { Provider } from "../Provider";

function isAssignable(value: any, paramType: string) {
	return true;
}

export class DatamartImpl implements Datamart {
	private _provider: Provider;
	private _id: string;
	private _name: string;
	private _params: { [key: string]: Parameter };
	private _config: any;

	constructor(id: string, name: string, provider: Provider, params: { [key: string]: Parameter }, config?: any) {
		requireThis(!!id && id.split(".").length === 2, "valid id");
		requireThis(!!name, "valid name");
		requireThis(!!provider, "valid provider");
		this._id = id;
		this._name = name;
		this._provider = provider;
		this._params = params;
		this._config = config;
	}

	get id() {
		return this._id;
	}

	get name() {
		return this._name;
	}

	get module() {
		return this._id.split(".")[0];
	}

	get datamart() {
		return this._id.split(".")[1];
	}

	get provider() {
		return this._provider;
	}

	get params() {
		return this._params;
	}

	get config() {
		return this._config;
	}

	validateValues = (params: { [key: string]: any }) => {
		let validationResult = {};
		Object.keys(params).forEach((key) => {
			const value = params[key];
			const param = this.params[key];
			if (!param) {
				validationResult[key] = "unknown parameter";
			} else if (!isAssignable(value, param.type)) {
				validationResult[key] =
					"wrong data type parameter (expected " + param.type + ", got: " + JSON.stringify(value) + ")";
			}
		});
		if (Object.keys(validationResult).length > 0) {
			throw new Error(JSON.stringify(validationResult, null, 2));
		}
	};
}
