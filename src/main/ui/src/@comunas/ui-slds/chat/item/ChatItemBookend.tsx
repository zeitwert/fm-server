import React from "react";

interface ChatItemBookendProps {
	content: React.ReactElement;
	figure?: React.ReactElement;
	isStop?: boolean;
}

export class ChatItemBookend extends React.Component<ChatItemBookendProps> {
	render() {
		const { content, figure, isStop } = this.props;
		return (
			<li className="slds-chat-listitem slds-chat-listitem_bookend">
				<div className={"slds-chat-bookend" + (isStop ? " slds-chat-bookend_stop" : "")}>
					{figure && (
						<span className="slds-icon_container slds-icon-utility-chat slds-chat-icon">{figure}</span>
					)}

					<p>{content}</p>
				</div>
			</li>
		);
	}
}
