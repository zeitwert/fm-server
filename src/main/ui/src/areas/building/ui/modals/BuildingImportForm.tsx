
import { Button, Card, Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import { getImportEntityText } from "lib/item/ui/ItemUtils";
import { action, computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingImportFormProps {
	onCancel: () => void;
	onImport: (building: string) => void;
}

const AGGREGATE = "zeitwert/building";
const VERSION = "1.0";

@observer
export default class BuildingImportForm extends React.Component<BuildingImportFormProps> {

	@observable content: any = null;
	@action setContent = (content: any) => {
		this.content = null;
		try {
			this.content = JSON.parse(content);
		} catch (error) {
			this.content = "Invalid content";
		}
	}
	@computed get hasContent() {
		return !!this.content;
	}
	@computed get isValidContent() {
		if (!this.hasContent) {
			return false;
		}
		const meta = this.content?.meta;
		return meta?.aggregate === AGGREGATE && meta?.version === VERSION;
	}

	constructor(props: BuildingImportFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const entityType = EntityTypes[EntityType.BUILDING];
		const content = toJS(this.content);
		const heading = (
			<MediaObject
				body={<>{getImportEntityText(entityType)}</>}
				figure={<Icon category={entityType.iconCategory} name={entityType.iconName} size="small" />}
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
						<Card heading="Dateiauswahl" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<input type="file" accept=".zwbd" onChange={this.showFile} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						{
							this.hasContent &&
							<Card heading="Inhalt" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body slds-card__body_inner">
									<div className="slds-form" role="list">
										{
											this.hasContent && !this.isValidContent &&
											<div className="slds-notify slds-notify_alert slds-alert_warning" role="alert">
												<span className="slds-icon_container slds-icon-utility-warning slds-m-right_x-small" title="Description of icon when needed">
													<svg className="slds-icon slds-icon_x-small" aria-hidden="true">
														<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#warning"></use>
													</svg>
												</span>
												<h2>This does not seem to be a valid zeitwert file.</h2>
											</div>
										}
										{
											this.isValidContent &&
											<dl className="slds-dl_horizontal">
												<dt className="slds-dl_horizontal__label">Immobilie:</dt>
												<dd className="slds-dl_horizontal__detail">{content?.name}</dd>
												<dt className="slds-dl_horizontal__label">Nr:</dt>
												<dd className="slds-dl_horizontal__detail">{content?.buildingNr}</dd>
												<dt className="slds-dl_horizontal__label">Addresse:</dt>
												<dd className="slds-dl_horizontal__detail">{content?.street}<br />{content?.zip} {content?.city}</dd>
											</dl>
										}
									</div>
								</div>
							</Card>
						}
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
				const content = e.target?.result;
				this.setContent(content);
			}
		};
		e.target?.files?.[0] && reader.readAsText(e.target?.files?.[0]);
	}

}
