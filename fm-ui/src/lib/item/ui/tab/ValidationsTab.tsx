import { Validation } from "@zeitwert/ui-model/ddd/aggregate/model/AggregateMeta";
import { observer } from "mobx-react";
import React from "react";

interface ValidationsTabProps {
	validations: Validation[];
}

@observer
export default class ValidationsTab extends React.Component<ValidationsTabProps> {

	render() {
		return (
			<div className="slds-m-around_medium">
				<p><b>{this.props.validations.length} Fehler</b></p>
				<ul className="slds-list_dotted">
					{
						this.props.validations.map((e, index) => <li key={"v-" + index}>{e.validation}</li>)
					}
				</ul>
			</div>
		);
	}

}
