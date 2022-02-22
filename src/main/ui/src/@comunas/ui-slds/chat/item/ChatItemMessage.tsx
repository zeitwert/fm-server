import React from "react";

export interface ChatItemMessageReply {
	title: string;
	text: JSX.Element | JSX.Element[];
}

interface ChatItemMessageProps {
	avatar?: React.ReactElement;
	icon?: React.ReactElement;
	reply?: ChatItemMessageReply;
	message: JSX.Element | JSX.Element[];
	meta?: JSX.Element;
	isOutbound?: boolean;
	className?: string;
}

export class ChatItemMessage extends React.Component<ChatItemMessageProps> {
	render() {
		const { avatar, icon, reply, message, meta, isOutbound, className } = this.props;
		const classes = [];
		classes.push("slds-chat-listitem");
		classes.push(isOutbound ? "slds-chat-listitem_outbound" : "slds-chat-listitem_inbound");
		if (className) {
			classes.push(className.split(" "));
		}

		return (
			<li className={classes.join(" ")}>
				<div className="slds-chat-message">
					{avatar && !isOutbound && <div className="slds-chat-avatar">{avatar}</div>}
					<div className="slds-chat-message__body">
						<div
							className={
								"slds-chat-message__text" +
								(isOutbound ? " slds-chat-message__text_outbound" : " slds-chat-message__text_inbound")
							}
						>
							{reply && (
								<div className="chat-message-reply">
									<div className="chat-message-reply__from">{reply.title}</div>
									<div className="chat-message-reply__text">{reply.text}</div>
								</div>
							)}
							{message}
						</div>
						{meta && (
							<div className={"slds-chat-message__meta " + (isOutbound ? "slds-text-align_right" : "")}>
								{icon && !isOutbound && <span className="slds-p-right_small">{icon}</span>}
								{meta}
								{icon && isOutbound && <span className="slds-p-left_small">{icon}</span>}
							</div>
						)}
					</div>
					{avatar && isOutbound && (
						<div className="slds-chat-avatar slds-m-right_none slds-m-left_small">{avatar}</div>
					)}
				</div>
			</li>
		);
	}
}
