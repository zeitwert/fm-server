
import { Config, EntityTypes } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

export interface TabProjectionPrintProps {
	itemType: "building" | "portfolio";
	itemId: string;
}

@observer
export default class TabProjectionPrint extends React.Component<TabProjectionPrintProps> {

	// @observable
	// urlObject: string | undefined;

	// constructor(props: TabProjectionPrintProps) {
	// 	super(props);
	// 	makeObservable(this);
	// }

	// async componentDidMount(): Promise<void> {
	// 	const { itemType, itemId } = this.props;
	// 	const labelSingular = EntityTypes[itemType].labelSingular;
	// 	const url = Config.getRestUrl(itemType, itemType + "s/" + itemId + `/evaluation/Auswertung ${labelSingular}?format=pdf&inline=true#view=fit`);
	// 	const res = await API.get(url, { responseType: "blob" });
	// 	const blob = await res.data;
	// 	this.urlObject = URL.createObjectURL(blob);
	// }

	render() {
		const { itemType, itemId } = this.props;
		const labelSingular = EntityTypes[itemType].labelSingular;
		const url = Config.getRestUrl(itemType, itemType + "s/" + itemId + `/evaluation/Auswertung ${labelSingular}?format=pdf&inline=true#view=fit`);
		return <iframe src={url} title="Auswertung" height="100%" width="100%" />;
	}

}
