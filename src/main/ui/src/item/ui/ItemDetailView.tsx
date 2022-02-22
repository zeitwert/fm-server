
import Card from "@salesforce/design-system-react/components/card";
import { FormWrapper } from "@zeitwert/ui-forms";
import { observer } from "mobx-react";
import React from "react";

interface ItemDetailProps {
	formId: string;
	itemAlias: string;
	itemSnapshot: any;
}

@observer
export default class ItemDetailView extends React.Component<ItemDetailProps> {
	render() {
		return (
			<Card heading="" hasNoHeader>
				<FormWrapper
					formId={this.props.formId}
					payload={{
						[this.props.itemAlias]: this.props.itemSnapshot,
						control: {
							enabled: false
						}
					}}
					displayMode="readonly"
				/>
			</Card>
		);
	}
}
