
import { Config, EntityTypes } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

export interface TabProjectionPrintProps {
	itemType: "building" | "portfolio";
	itemId: string;
}

@observer
export default class TabProjectionPrint extends React.Component<TabProjectionPrintProps> {

	render() {
		const url = Config.getRestUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + `/evaluation/Auswertung ${EntityTypes[this.props.itemType].labelSingular}?format=pdf&inline=true#view=fit`);
		return <iframe src={url} title="Auswertung" height="100%" width="100%" />;
	}

}
