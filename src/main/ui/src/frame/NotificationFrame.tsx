import { Alert, AlertContainer, Toast, ToastContainer } from "@salesforce/design-system-react";
import { makeObservable, observable } from "mobx";
import { observer, Provider } from "mobx-react";
import React from "react";

const TOAST_DURATION = 2000;

interface NotificationFrameProps {
	showToast?: (variant: string, message: string) => void;
	showAlert?: (variant: string, message: string) => void;
	children?: JSX.Element;
}

@observer
export default class NotificationFrame extends React.Component<NotificationFrameProps> {
	@observable alertVariant?: any;
	@observable alertMessage?: string;
	@observable toastVariant?: any;
	@observable toastMessage?: string;

	constructor(props: NotificationFrameProps) {
		super(props);
		makeObservable(this);

		this.showAlert = this.showAlert.bind(this);
		this.closeAlert = this.closeAlert.bind(this);
		this.showToast = this.showToast.bind(this);
		this.closeToast = this.closeToast.bind(this);
	}

	private showToast(variant: any, message: string) {
		this.toastVariant = variant;
		this.toastMessage = message;
	}

	private closeToast() {
		this.toastVariant = undefined;
		this.toastMessage = undefined;
	}

	private showAlert(variant: any, message: string) {
		this.alertVariant = variant;
		this.alertMessage = message;
	}

	private closeAlert() {
		this.alertVariant = undefined;
		this.alertMessage = undefined;
	}

	render() {
		return (
			<Provider showAlert={this.showAlert} showToast={this.showToast}>
				{this.props.children}
				<ToastContainer className="slds-clearfix">
					{this.toastVariant && (
						<Toast
							variant={this.toastVariant}
							labels={{
								heading: [this.toastMessage]
							}}
							duration={TOAST_DURATION}
							onRequestClose={this.closeToast}
							className="slds-float_right"
						/>
					)}
				</ToastContainer>
				<AlertContainer>
					{this.alertVariant && (
						<Alert
							variant={this.alertVariant}
							labels={{
								heading: this.alertMessage
							}}
							onRequestClose={this.closeAlert}
							dismissible
						/>
					)}
				</AlertContainer>
			</Provider>
		);
	}
}
