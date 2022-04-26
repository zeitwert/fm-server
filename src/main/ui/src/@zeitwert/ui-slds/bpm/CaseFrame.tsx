import { Button, Spinner } from "@salesforce/design-system-react";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface CaseFrameProps extends RouteComponentProps {
	url: string;
	backLink?: string;
}

@observer
class CaseFrame extends React.Component<CaseFrameProps> {
	@observable iframe?: HTMLIFrameElement;
	@observable isLoading = true;

	constructor(props: CaseFrameProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
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
	}

	render() {
		return (
			<div className="slds-is-relative">
				{this.isLoading && <Spinner variant="brand" />}
				<div>
					<iframe
						title="Case Frame"
						sandbox="allow-scripts allow-same-origin allow-forms"
						ref={(f: HTMLIFrameElement) => (this.iframe = f)}
						src={this.props.url}
						className="fa-full-dims"
						style={{
							display: this.isLoading ? "none" : "block",
							minHeight: "400px",
							border: "none"
						}}
					/>
				</div>
				<Button variant="brand" label="OK" onClick={this.handleOk} />
				<Button variant="brand" label="Discard" onClick={this.handleDiscard} />
			</div>
		);
	}

	private close = () => {
		this.setIsLoading(false);

		if (this.props.backLink) {
			this.props.navigate(this.props.backLink);
		}
	};

	private setIsLoading = (isLoading: boolean) => {
		this.isLoading = isLoading;
	};

	private handleOk = (event: any) => {
		this.sendPostMessage("ok");
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

export default withRouter(CaseFrame);
