import React, { PropsWithChildren } from "react";

interface TextSelectionProps {
	onSelect(selection: string): void;
}

/**
 * Trigger selection callback on right mouse click for current text selection
 */
export class TextSelection extends React.Component<PropsWithChildren<TextSelectionProps>> {
	render() {
		return <div onContextMenu={this.selectText}>{this.props.children}</div>;
	}

	private selectText = (e: any) => {
		e.preventDefault();
		const sel: any = window.getSelection()!.toString();
		if (sel) {
			this.props.onSelect(sel);
		}
	};
}
