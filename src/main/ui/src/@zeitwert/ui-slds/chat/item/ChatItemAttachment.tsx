import { Icon } from "@salesforce/design-system-react";
import React from "react";

interface ChatItemAttachmentProps {
	name: string;
	icon?: React.ReactElement;
	link: string;
	onLinkClick?: (e: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => void;
}

export class ChatItemAttachment extends React.Component<ChatItemAttachmentProps> {
	render() {
		const { name, link, onLinkClick } = this.props;
		return (
			<span>
				<span className="slds-icon_container slds-icon-doctype-attachment slds-chat-icon" title="Attachment">
					{this.getIcon()}
					<span className="slds-assistive-text">Attachment</span>
				</span>
				<a
					href={link}
					target="_blank"
					rel="noopener noreferrer"
					className="slds-p-right_x-small"
					onClick={onLinkClick}
				>
					{name}
				</a>
			</span>
		);
	}

	private getIcon() {
		const { icon } = this.props;
		if (!icon) {
			return <Icon category="doctype" name="attachment" size="x-small" />;
		}

		return icon;
	}
}
