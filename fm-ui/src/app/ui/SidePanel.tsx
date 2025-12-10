
import { observer } from "mobx-react";
import React from "react";

export interface SidePanelProps {
	children: any;
	style?: any;
}

const SidePanel: React.FC<SidePanelProps> = observer((props) => {
	return (
		<section
			aria-labelledby="panel-heading-id"
			className="slds-popover slds-popover_panel slds-popover_large slds-popover_prompt_top-right slds-popover_prompt_bottom-right"
			style={props.style}
			role="dialog"
		>
			{props.children}
		</section>
	);
});

export default SidePanel;
