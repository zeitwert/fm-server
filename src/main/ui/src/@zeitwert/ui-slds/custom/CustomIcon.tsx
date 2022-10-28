
import { Icon } from "@salesforce/design-system-react";
import { inject } from "mobx-react";
import React from "react";

const CUSTOM_CATEGORY = "zeitwert";

interface CustomIconProps {
	category: string;
	name: string;
	path?: string;
	containerClassName?: string;
	className?: string;
	size: string;
}

/**
 * Custom Component that wraps SLDS icon component to allow custom sprites by the use of path.
 */
@inject("session")
export default class CustomIcon extends React.Component<CustomIconProps> {
	render() {
		const props = Object.assign({}, this.props) as any;
		if (props.category === CUSTOM_CATEGORY) {
			delete props.category;
			return <Icon {...props} path={(props.path ? props.path : "/assets/icons-custom/sprite.svg") + "#" + props.name} />;
		}
		return <Icon {...props} />;
	}
}
