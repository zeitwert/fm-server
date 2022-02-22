import React from "react";

interface ChatProps {}

export class Chat extends React.Component<ChatProps> {
	render() {
		const { children } = this.props;
		return (
			<section role="log" className="slds-chat">
				{children}
			</section>
		);
	}
}
