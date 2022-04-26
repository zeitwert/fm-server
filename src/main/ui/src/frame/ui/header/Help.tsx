import { GlobalHeaderHelp, Popover } from "@salesforce/design-system-react";
import { GLOBAL_HEADER_HELP } from "@salesforce/design-system-react/utilities/constants";
import React from "react";

const ipsum =
	"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec bibendum fermentum eros, vel porta metus dignissim vitae. Fusce finibus sed magna vitae tempus. Suspendisse condimentum, arcu eu viverra vulputate, mauris odio dictum velit, in dictum lorem augue id augue. Proin nec leo convallis, aliquet mi ut, interdum nunc.";

export default class Help extends React.Component {
	static displayName = GLOBAL_HEADER_HELP;
	render() {
		return (
			<GlobalHeaderHelp
				popover={
					<Popover
						id="header-help-popover-id"
						heading="Help and Training"
						align="top right"
						ariaLabelledby="help-heading"
						body={ipsum}
					>
						{" "}
					</Popover>
				}
			/>
		);
	}
}
