import { Button, Modal } from "@salesforce/design-system-react";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface CaseModalProps extends RouteComponentProps {
	title: string;
	url: string;
	backLink: string;
	buttonType: string;
	usePostMessage: boolean;
}

@observer
class CaseModal extends React.Component<CaseModalProps> {
	@observable iframe?: HTMLIFrameElement;
	@observable isLoading = true;

	constructor(props: CaseModalProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		if (this.props.usePostMessage) {
			// @ts-ignore
			const eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
			const eventer = window[eventMethod];
			const messageEvent = eventMethod === "attachEvent" ? "onmessage" : "message";
			eventer(
				messageEvent,
				(event: any) => {
					if (event.data === "close") {
						this.close();
					} else if (event.data === "ready") {
						this.setIsLoading(false);
					} else if (event.data === "loading") {
						this.setIsLoading(true);
					}
				},
				false
			);
		} else {
			this.setIsLoading(false);
		}
	}

	render() {
		return (
			<Modal
				size="small"
				heading={
					<div className="slds-grid slds-grid_align-spread slds-m-horizontal_small slds-grid_vertical-align-center slds-text-body_regular">
						<h2 className="slds-text-heading_medium">{this.props.title}</h2>
					</div>
				}
				footer={
					this.props.usePostMessage && (
						<>
							{this.props.buttonType === "ok-cancel" && (
								<>
									<Button variant="brand" label="OK" onClick={this.handleOk} />
									<Button variant="brand" label="Cancel" onClick={this.handleCancel} />
								</>
							)}
							{this.props.buttonType === "ok-discard" && (
								<>
									<Button variant="brand" label="OK" onClick={this.handleOk} />
									<Button variant="brand" label="Discard" onClick={this.handleDiscard} />
								</>
							)}
							{this.props.buttonType === "complete-save-cancel" && (
								<>
									<Button variant="brand" label="Complete" onClick={this.handleComplete} />
									<Button variant="brand" label="Save" onClick={this.handleSave} />
									<Button variant="brand" label="Cancel" onClick={this.handleCancel} />
								</>
							)}
						</>
					)
				}
				onRequestClose={this.onClose}
				isOpen
			>
				<iframe
					title="Case Modal"
					ref={(f: HTMLIFrameElement) => (this.iframe = f)}
					src={this.props.url}
					className="fa-full-dims"
					style={{
						display: this.isLoading ? "none" : "block",
						minHeight: 800,
						border: "none"
					}}
				/>
				{/*this.isLoading && <Spinner variant="brand" />*/}
			</Modal>
		);
	}

	private close = () => {
		this.props.navigate(this.props.backLink);
	};

	private onClose = () => {
		this.setIsLoading(false);
		this.close();
	};

	private setIsLoading = (isLoading: boolean) => {
		this.isLoading = isLoading;
	};

	private handleComplete = (event: any) => {
		this.sendPostMessage("complete");
		event.preventDefault();
	};

	private handleSave = (event: any) => {
		this.sendPostMessage("save");
		event.preventDefault();
	};

	private handleOk = (event: any) => {
		this.sendPostMessage("ok");
		event.preventDefault();
	};

	private handleCancel = (event: any) => {
		this.sendPostMessage("cancel");
		event.preventDefault();
	};

	private handleDiscard = (event: any) => {
		this.sendPostMessage("discard");
		event.preventDefault();
	};

	private sendPostMessage = (msg: string) => {
		this.iframe?.contentWindow?.postMessage(msg, "*");
	};
}

export default withRouter(CaseModal);
