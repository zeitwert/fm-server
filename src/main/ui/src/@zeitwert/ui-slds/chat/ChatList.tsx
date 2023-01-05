import React, { PropsWithChildren } from "react";

interface ChatListProps {
	isSelected?: boolean;
	isClickable?: boolean;
	onClick?: () => void;
}

export class ChatList extends React.Component<PropsWithChildren<ChatListProps>> {
	get isClickable() {
		return this.props.isClickable && this.props.onClick;
	}

	render() {
		const { isSelected, onClick, children } = this.props;

		return (
			<ul
				className={"slds-chat-list" + (isSelected ? " selected" : "")}
				onClick={() => this.isClickable && onClick && onClick()}
				style={this.isClickable ? { cursor: "pointer" } : {}}
			>
				{children}
			</ul>
		);
	}
}
