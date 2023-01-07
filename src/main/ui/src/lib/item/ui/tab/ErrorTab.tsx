import { Validation } from "@zeitwert/ui-model/ddd/aggregate/model/AggregateMeta";
import { observer } from "mobx-react";
import React from "react";

interface ErrorTabProps {
	validationList: Validation[];
}

@observer
export default class ErrorTab extends React.Component<ErrorTabProps> {

	render() {
		return (
			<div className="slds-m-around_medium">
				<p><b>{this.props.validationList.length} Fehler</b></p>
				<ul className="slds-list_dotted">
					{
						this.props.validationList.map((e, index) => <li key={"v-" + index}>{e.validation}</li>)
					}
				</ul>
			</div>
		);
	}

}
