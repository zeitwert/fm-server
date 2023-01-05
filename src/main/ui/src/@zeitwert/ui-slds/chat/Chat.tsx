import React, { PropsWithChildren } from "react";

interface ChatProps { }

export class Chat extends React.Component<PropsWithChildren<ChatProps>> {
	render() {
		const { children } = this.props;
		return (
			<section role="log" className="slds-chat">
				{children}
			</section>
		);
	}
}
