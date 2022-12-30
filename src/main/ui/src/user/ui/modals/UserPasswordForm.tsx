
import { Button, Card, Checkbox, Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input } from "@zeitwert/ui-forms";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import { action, computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

const MIN_PASSWORD_LENGTH = 8;

export interface UserPasswordFormProps {
	onCancel: () => void;
	onClose: (password: string, requestChange: boolean) => void;
}

@observer
export default class UserPasswordForm extends React.Component<UserPasswordFormProps> {

	@observable password: string | undefined;
	@observable requestChange: boolean = false;

	@action setPassword = (password: any) => {
		this.password = password;
	}
	@action toggleChange = () => {
		this.requestChange = !this.requestChange;
	}

	@computed get isValidPassword() {
		return (this.password?.length || 0) >= MIN_PASSWORD_LENGTH;
	}

	constructor(props: UserPasswordFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const entityType = EntityTypes[EntityType.USER];
		const heading = (
			<MediaObject
				body="Passwort ändern"
				figure={<Icon category={entityType.iconCategory} name={entityType.iconName} size="small" />}
				verticalCenter
			/>
		);
		const buttons = (
			<>
				<Button onClick={this.props.onCancel}>Abbrechen</Button>
				<Button
					variant="brand"
					onClick={() => this.props.onClose(this.password!, this.requestChange)}
					disabled={!this.isValidPassword}
				>Ändern</Button>
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
						<Card hasNoHeader bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<div className="slds-size_3-of-12" />
											<Input label="Neues Passwort" type="text" onChange={(e: any) => { this.setPassword(e.target.value) }} size={6} />
										</FieldRow>
										<FieldRow>
											<div className="slds-size_3-of-12" />
											<div className="slds-size_6-of-12">
												<div className="slds-form-element">
													<span className="slds-form-element__label" style={{ whiteSpace: "nowrap" }}>&nbsp;</span>
													<Checkbox
														labels={{
															label: "Passwortänderung verlangen bei nächstem Login",
															toggleDisabled: "Nein",
															toggleEnabled: "Ja"
														}}
														checked={this.requestChange}
														onChange={this.toggleChange}
														size={6}
														variant="toggle" />
												</div>
											</div>
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

}
