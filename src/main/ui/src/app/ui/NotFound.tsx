
import { Card } from "@salesforce/design-system-react";
import { EntityTypeInfo } from "@zeitwert/ui-model";
import React from "react";

export interface NotFoundProps {
	entityType: EntityTypeInfo;
	id: string;
}

export default class NotFound extends React.Component<NotFoundProps> {

	render() {
		return (
			<Card header={
				<div className="slds-notify slds-notify_alert slds-alert_warning" role="alert">
					<span className="slds-icon_container slds-icon-utility-warning slds-m-right_x-small">
						<svg className="slds-icon slds-icon_x-small" aria-hidden="true">
							<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#warning"></use>
						</svg>
					</span>
					<h2>Objekt nicht gefunden.</h2>
				</div>
			}>
				<div className="slds-card__body slds-card__body_inner">
					{` ${this.props.entityType.labelSingular} mit der Id ${this.props.id} wurde nicht gefunden.`}
				</div>
			</Card>
		);
	}

}
