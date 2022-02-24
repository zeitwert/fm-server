import Button from "@salesforce/design-system-react/components/button";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface State {
	icon?: any;
	label: string | React.ReactNode;
}

interface ButtonStatefulProps {
	stateOne: State;
	stateTwo?: State;
	stateThree?: State;
	active?: boolean;
	onClick?: () => void;
	variant?: string;
}

/**
 * Custom Button Statefull that has different customization options.
 * - Used for favorites, follow.
 */
@observer
export default class ButtonStateful extends React.Component<ButtonStatefulProps> {
	@observable isHovering = false;

	constructor(props: ButtonStatefulProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { stateOne, stateTwo, stateThree, active, onClick } = this.props;

		if (active && stateThree) {
			const iconProps = stateThree.icon
				? {
						iconCategory: "utility" as any,
						iconName: stateThree.icon,
						iconPosition: "left" as any
				  }
				: {};
			return (
				<Button
					variant="brand"
					onClick={() => onClick && onClick()}
					onMouseEnter={() => (this.isHovering = true)}
					onMouseLeave={() => (this.isHovering = false)}
					{...iconProps}
				>
					{this.isHovering && stateTwo ? stateTwo.label : stateThree.label}
				</Button>
			);
		}

		return (
			<Button
				onClick={() => onClick && onClick()}
				onMouseEnter={() => (this.isHovering = true)}
				onMouseLeave={() => (this.isHovering = false)}
			>
				{this.isHovering && stateTwo ? stateTwo.label : stateOne.label}
			</Button>
		);
	}
}
