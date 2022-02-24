import Icon from "@salesforce/design-system-react/components/icon";
import { EmailProvider } from "@zeitwert/ui-model";
import { AppCtx } from "App";
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
	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const props = Object.assign({}, this.props) as any;

		// Replace generic email icon with external provider icon.
		const { category, name, path } = props;
		if (category === "standard" && name === "email") {
			switch (this.ctx.session.sessionInfo?.user.emailProvider.id) {
				case EmailProvider.GOOGLE:
					props.category = CUSTOM_CATEGORY;
					props.name = "gmail";
					break;
				case EmailProvider.MICROSOFT:
					props.category = CUSTOM_CATEGORY;
					props.name = "outlook";
					break;
				default:
					break;
			}
		}

		if (props.category === CUSTOM_CATEGORY) {
			delete props.category;
			return <Icon {...props} path={(path ? path : "/assets/icons-custom/sprite.svg") + "#" + props.name} />;
		}

		return <Icon {...props} />;
	}
}
