import classNames from "classnames";
import React from "react";

export enum ChatItemBlockType {
	GENERIC = "generic",
}

interface ChatItemBlockProps {
	type: ChatItemBlockType;
	avatar?: React.ReactElement;
	icon?: React.ReactElement;
	message: JSX.Element | JSX.Element[];
	meta?: JSX.Element;
}

export class ChatItemBlock extends React.Component<ChatItemBlockProps> {
	render() {
		const { type, avatar, icon, message, meta } = this.props;
		const classes = classNames("chat-block", "chat-block-type__" + type);
		return (
			<li className="slds-chat-listitem">
				<div className={classes}>
					{avatar && <div className="slds-chat-avatar">{avatar}</div>}
					<div className="chat-block__body">
						<div className="chat-block__text">{message}</div>
						{meta && (
							<div className="slds-chat-message__meta">
								{icon && <span className="slds-p-right_small">{icon}</span>}
								{meta}
							</div>
						)}
					</div>
				</div>
			</li>
		);
	}
}
