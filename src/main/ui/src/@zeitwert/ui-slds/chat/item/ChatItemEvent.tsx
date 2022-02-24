import React from "react";

interface ChatItemEventProps {
	figure?: React.ReactElement;
	body: JSX.Element | JSX.Element[];
	message?: string;
}

export class ChatItemEvent extends React.Component<ChatItemEventProps> {
	render() {
		const { figure, body, message } = this.props;
		return (
			<li className="slds-chat-listitem slds-chat-listitem_event">
				<div className="slds-chat-event">
					<div className="slds-chat-event__body">
						{figure && (
							<span className="slds-icon_container slds-icon-utility-change_owner slds-chat-icon">
								{figure}
							</span>
						)}
						{body}
					</div>
					{message && <div className="slds-chat-event__agent-message">{message}</div>}
				</div>
			</li>
		);
	}
}
