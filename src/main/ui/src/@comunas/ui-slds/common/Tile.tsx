import Icon from "@salesforce/design-system-react/components/icon";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import React from "react";
import { Link } from "react-router-dom";

interface TileProps {
	title: string;
	link?: string;
	iconCategory?: string;
	iconName?: string;
}

export class Tile extends React.Component<TileProps> {
	render() {
		const { iconCategory, iconName } = this.props;
		return (
			<MediaObject
				className="slds-tile slds-card__tile slds-hint-parent"
				figure={
					iconCategory && iconName ? (
						<Icon category={iconCategory as any} name={iconName} size="small" />
					) : null
				}
				body={this.renderBody()}
			/>
		);
	}

	private renderBody() {
		const { title, link, children } = this.props;
		return (
			<>
				<div className="slds-grid slds-grid_align-spread slds-has-flexi-truncate">
					<h3 className="slds-tile__title slds-truncate" title={title}>
						{link && <Link to={link}>{title}</Link>}
						{!link && title}
					</h3>
				</div>
				<div className="slds-tile__detail">
					<dl className="slds-list_horizontal slds-wrap">{children}</dl>
				</div>
			</>
		);
	}
}
