
import { Config } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

export interface TabProjectionPrintProps {
	itemType: "building" | "portfolio";
	itemId: string;
	fileName: string;
}

@observer
export default class TabProjectionPrint extends React.Component<TabProjectionPrintProps> {

	render() {
		const fileName = this.props.fileName;
		const url = Config.getRestUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + `/evaluation/${fileName}?format=pdf&inline=true#view=fit`);
		return <iframe src={url} title="Auswertung" height="100%" width="100%" />;
	}

}
