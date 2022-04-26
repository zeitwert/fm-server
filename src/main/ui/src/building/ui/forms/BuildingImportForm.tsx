
import { Button, Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import Card from "@salesforce/design-system-react/components/card";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingImportFormProps {
	onCancel: () => void;
	onImport: (building: string) => void;
}

@observer
export default class BuildingImportForm extends React.Component<BuildingImportFormProps> {

	@observable hasContent: boolean = false;
	@observable content: string = "";

	constructor(props: BuildingImportFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const heading = (
			<MediaObject
				body={
					<>Import</>
				}
				figure={<Icon category={EntityTypes[EntityType.BUILDING].iconCategory} name={EntityTypes[EntityType.BUILDING].iconName} size="small" />}
				verticalCenter
			/>
		);
		const buttons = (
			<>
				<Button onClick={this.props.onCancel}>Cancel</Button>
				<Button variant="brand" onClick={() => this.props.onImport(this.content)} disabled={!this.hasContent}>
					Import
				</Button>
			</>
		);
		return (
			<Modal
				heading={heading}
				onRequestClose={this.props.onCancel}
				dismissOnClickOutside={false}
				footer={buttons}
				ariaHideApp={false}
				size="small"
				isOpen
			>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="File" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<input type="file" accept=".zwbd" onChange={(e) => this.showFile(e)} />
										</FieldRow>
										<FieldRow>
											<pre>{this.content ? JSON.stringify(JSON.parse(this.content), null, 2) : ""}</pre>
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
			</Modal>
		);
	}

	private showFile = async (e: React.ChangeEvent<HTMLInputElement>) => {
		e.preventDefault();
		const reader = new FileReader();
		reader.onload = async (e) => {
			if (typeof e.target?.result === "string") {
				this.hasContent = true;
				this.content = e.target?.result;
			}
		};
		e.target?.files?.[0] && reader.readAsText(e.target?.files?.[0]);
	}

}
