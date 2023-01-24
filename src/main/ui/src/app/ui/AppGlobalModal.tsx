
import { Card, Spinner } from "@salesforce/design-system-react";
import classNames from "classnames";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React, { PropsWithChildren } from "react";
import ReactDOM from "react-dom";
import AppBreadCrumbs, { BreadCrumb } from "./AppBreadCrumbs";

export interface AppGlobalModalProps {
	path: BreadCrumb[];
	title?: React.ReactNode;
	onPrimaryAction?: () => void;
	onSecondaryAction?: () => void;
	onClose?: () => void;
}

@observer
export default class AppGlobalModal extends React.Component<PropsWithChildren<AppGlobalModalProps>> {

	@observable breadCrumbs: BreadCrumb[] = [];
	@observable isActionProcessing = false;
	@observable isActionProcessed = false;

	constructor(props: AppGlobalModalProps) {
		super(props);
		makeObservable(this);
		this.addBreadCrumbs(this.props.path);
	}

	componentWillMount() {
		window.addEventListener("keydown", this.handleKeyDown);
	}

	componentWillUnmount() {
		window.removeEventListener("keydown", this.handleKeyDown);
	}

	render() {
		const { title, children } = this.props;
		const classes = classNames("fa-global-modal", this.isActionProcessed ? "slide-out" : "");
		return ReactDOM.createPortal(
			<div className={classes}>
				{this.isActionProcessing && <Spinner variant="brand" size="large" />}
				<AppBreadCrumbs
					items={this.breadCrumbs}
					isActionProcessing={this.isActionProcessing}
					onPrimaryAction={this.onPrimaryAction}
					onSecondaryAction={this.onSecondaryAction}
				/>
				<div className="slds-p-around_small" style={{ height: "calc(100% - 40px)" }}>
					<Card heading="" hasNoHeader className="fa-height-100" bodyClassName="slds-m-around_none">
						{title && (
							<div className="slds-text-heading_medium slds-text-align_center slds-m-around_medium">
								{title}
							</div>
						)}
						<div
							className="slds-scrollable_y"
							style={{ height: "calc(100% - " + (title ? "3.6rem" : "0rem") + ")" }}
						>
							{children}
						</div>
					</Card>
				</div>
			</div>,
			document.getElementById("root")!
		);
	}

	private handleKeyDown = (e: any) => {
		if (e.keyCode === 27) {
			this.onSecondaryAction();
		}
	};

	private addBreadCrumbs = (crumbs: BreadCrumb[]) => {
		this.breadCrumbs.push(...crumbs);
	}

	private removeBreadCrumbs = (number: number) => {
		this.breadCrumbs.splice(-number, number);
	}

	private onPrimaryAction = async () => {
		const { path, onPrimaryAction, onClose } = this.props;
		this.isActionProcessing = true;
		onPrimaryAction && (await onPrimaryAction());
		this.isActionProcessed = true;
		setTimeout(() => {
			onClose && onClose();
			this.removeBreadCrumbs(path.length);
		}, 300);
	};

	private onSecondaryAction = async () => {
		const { path, onSecondaryAction, onClose } = this.props;
		this.isActionProcessing = true;
		onSecondaryAction && (await onSecondaryAction());
		this.isActionProcessed = true;
		setTimeout(() => {
			onClose && onClose();
			this.removeBreadCrumbs(path.length);
		}, 300);
	};

}
