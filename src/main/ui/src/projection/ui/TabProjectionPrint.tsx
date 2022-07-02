
import { Config } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

export interface TabProjectionPrintProps {
	itemType: "building" | "portfolio";
	itemId: string;
}

@observer
export default class TabProjectionPrint extends React.Component<TabProjectionPrintProps> {

	render() {
		const url = Config.getEvaluationUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + "?format=pdf&inline=true");
		return <iframe src={url} title="Kosten" height="100%" width="100%" />;
	}

}
