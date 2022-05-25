
import { Button, ButtonGroup } from "@salesforce/design-system-react";
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
		return <>
			<ButtonGroup variant="list">
				<Button onClick={() => { window.location.href = Config.getEvaluationUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + "?format=pdf"); }}>Drucken PDF</Button>
			</ButtonGroup>
			<ButtonGroup variant="list">
				<Button onClick={() => { window.location.href = Config.getEvaluationUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + "?format=docx"); }}>Drucken DOCX</Button>
			</ButtonGroup>
		</>;
	}

}
