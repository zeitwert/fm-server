import { inject } from "mobx-react";
import React from "react";
import { AppCtx } from "./App";

interface ErrorBoundaryProps { }

interface ErrorBoundaryState {
	hasError: boolean;
}

@inject("showAlert")
export default class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ErrorBoundaryProps) {
		super(props);
		this.state = { hasError: false };
	}

	static getDerivedStateFromError() {
		return { hasError: true };
	}

	componentDidCatch(error: any) {
		if (error.message && error.message === "Network Error") {
			this.ctx.showAlert("error", "Could not connect with the server. Please wait a moment and reload the page.");
		} else {
			this.ctx.showAlert("error", "Unknown error: " + error);
		}
	}

	render() {
		if (!this.state.hasError) {
			return this.props.children;
		}
		return <></> as any;
	}
}
