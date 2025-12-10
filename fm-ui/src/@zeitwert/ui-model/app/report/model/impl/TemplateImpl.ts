import { requireThis } from "../../../common/utils/Invariants";
import { Datamart } from "../Datamart";
import { Layout } from "../Layout";
import { Template } from "../Template";

export class TemplateImpl implements Template {
	private _datamart: Datamart;
	private _id: string;
	private _name: string;
	private _params: Map<string, any>;
	private _layout: Layout;
	private _sort?: string;
	private _limit?: number;

	constructor(
		datamart: Datamart,
		layout: Layout,
		id: string,
		name: string,
		params: Map<string, any>,
		sort?: string,
		limit?: number
	) {
		requireThis(!!datamart, "valid datamart");
		requireThis(!!id && id.split(".").length === 3, "valid id");
		requireThis(!!name, "valid name");
		requireThis(!!layout, "valid layout");
		datamart.validateValues(params);
		this._datamart = datamart;
		this._id = id;
		this._name = name;
		this._params = params;
		this._layout = layout;
		this._sort = sort;
		this._limit = limit;
	}

	get datamart() {
		return this._datamart;
	}

	get id() {
		return this._id;
	}

	get name() {
		return this._name;
	}

	get params() {
		return this._params;
	}

	get layout() {
		return this._layout;
	}

	get sort() {
		return this._sort;
	}

	get limit() {
		return this._limit;
	}
}
