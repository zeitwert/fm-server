import { requireThis } from "../../../common/utils/Invariants";
import { Datamart } from "../Datamart";
import { Layout, LayoutType } from "../Layout";

export class LayoutImpl implements Layout {
	private _datamart: Datamart;
	private _id: string;
	private _name: string;
	private _layout: any;

	constructor(datamart: Datamart, id: string, name: string, layout: any) {
		requireThis(!!datamart, "valid datamart");
		requireThis(!!id && id.split(".").length === 3, "valid id");
		requireThis(!!name, "valid name");
		requireThis(!!layout, "valid layout");
		this._datamart = datamart;
		this._id = id;
		this._name = name;
		this._layout = layout;
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

	get layoutType(): LayoutType {
		return this._layout.layoutType;
	}

	get layout() {
		return this._layout;
	}
}
