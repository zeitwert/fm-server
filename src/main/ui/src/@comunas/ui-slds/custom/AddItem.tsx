import { Grid } from "@comunas/ui-slds/common/Grid";
import Button from "@salesforce/design-system-react/components/button";
import Icon from "@salesforce/design-system-react/components/icon";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import classNames from "classnames";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface AddItemProps {
	className: string;
	title: string;
	onAdd: (title: string) => void;
}

@observer
export default class AddItem extends React.Component<AddItemProps> {
	@observable addItemTitle = "";
	@observable addItemVisible = false;

	constructor(props: AddItemProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { className, title } = this.props;
		if (!this.addItemVisible) {
			return (
				<div className={className}>
					<button className="fa-add-button" type="button" onClick={() => (this.addItemVisible = true)}>
						<MediaObject
							body={"Create new " + title}
							figure={<Icon category="utility" name="add" size="x-small" />}
							verticalCenter
						/>
					</button>
				</div>
			);
		}
		const classes = classNames("slds-grow", className);
		return (
			<Grid isVertical={false} className={classes}>
				<div className="slds-form-element slds-p-right_x-small fa-full-width">
					<div className="slds-form-element__control">
						<input
							className="slds-input"
							placeholder="Introduce a title..."
							type="text"
							value={this.addItemTitle}
							onChange={(event) => (this.addItemTitle = event.target.value)}
							onKeyDown={(event) => {
								if (event.keyCode === 13) {
									this.onAdd();
								}
							}}
							onBlur={(event) => {
								if (
									!(
										event.relatedTarget instanceof HTMLButtonElement &&
										event.relatedTarget.id === "fa-add-item-button"
									)
								) {
									this.addItemTitle = "";
									this.addItemVisible = false;
								}
							}}
							autoFocus
						/>
					</div>
				</div>
				<Button
					id="fa-add-item-button"
					className="slds-float_right"
					label="Add new"
					onClick={this.onAdd}
					variant="brand"
					disabled={!this.addItemTitle.length}
				/>
			</Grid>
		);
	}

	private onAdd = () => {
		const { onAdd } = this.props;
		onAdd(this.addItemTitle);
		this.addItemTitle = "";
		this.addItemVisible = false;
	};
}
