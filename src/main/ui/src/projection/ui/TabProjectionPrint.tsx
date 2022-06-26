
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
				<Button onClick={() => this.doExport("pdf")}>Drucken PDF</Button>
			</ButtonGroup>
			<ButtonGroup variant="list">
				<Button onClick={() => this.doExport("docx")}>Drucken DOCX</Button>
			</ButtonGroup>
		</>;
	}

	private doExport = async (format: string) => {
		window.location.href = Config.getEvaluationUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + "?format=" + format);
		// const response = await API.get(Config.getEvaluationUrl(this.props.itemType, this.props.itemType + "s/" + this.props.itemId + "?format=" + format));
		// const contentDisposition = response.headers["content-disposition"];
		// const filename = contentDisposition.match(/filename="(.+)"/)?.[1];
		// if (filename) {
		// 	const objectUrl = window.URL.createObjectURL(new Blob([response.data]));
		// 	const anchor = document.createElement("a");
		// 	try {
		// 		document.body.appendChild(anchor);
		// 		anchor.href = objectUrl;
		// 		anchor.download = filename;
		// 		anchor.click();
		// 	} finally {
		// 		document.body.removeChild(anchor);
		// 		//window.URL.revokeObjectURL(objectUrl);
		// 	}
		// }
	}

}
