
import { API, Config, EntityTypes } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface TabProjectionPrintProps {
	itemType: "building" | "portfolio";
	itemId: string;
}

@observer
export default class TabProjectionPrint extends React.Component<TabProjectionPrintProps> {

	@observable
	urlObject: string | undefined;

	constructor(props: TabProjectionPrintProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount(): Promise<void> {
		const { itemType, itemId } = this.props;
		const labelSingular = EntityTypes[itemType].labelSingular;
		const url = Config.getRestUrl(itemType, itemType + "s/" + itemId + `/evaluation/Auswertung ${labelSingular}?format=pdf&inline=true#view=fit`);
		const res = await API.get(url, { responseType: "blob" });
		const blob = await res.data;
		this.urlObject = URL.createObjectURL(blob);
	}

	render() {
		return <iframe src={this.urlObject} title="Auswertung" height="100%" width="100%" />;
	}

}
