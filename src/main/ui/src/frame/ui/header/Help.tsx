import { Button, GlobalHeaderHelp, Popover } from "@salesforce/design-system-react";
import { GLOBAL_HEADER_HELP } from "@salesforce/design-system-react/utilities/constants";
import { Config, session } from "@zeitwert/ui-model";
import React from "react";

export default class Help extends React.Component {
	static displayName = GLOBAL_HEADER_HELP;
	render() {
		return (
			<GlobalHeaderHelp
				popover={
					<Popover
						heading="Hilfe und Training"
						align="top right"
						footerWalkthroughActions={
							<Button onClick={() => { window.open(Config.getDocUrl(session.helpContext), '_blank')?.focus(); }} variant="neutral">
								Bedienungsanleitung öffnen
							</Button>
						}
					/>
				}
			/>
		);
	}
}
