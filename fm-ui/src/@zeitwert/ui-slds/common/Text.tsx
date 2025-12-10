import classNames from "classnames";
import React, { PropsWithChildren } from "react";

export enum TextCategory {
	body = "body",
	heading = "heading",
	title = "title"
}

export enum TextBodyType {
	regular = "regular",
	small = "small",
	caps = "caps"
}

export enum TextHeadingType {
	large = "large",
	medium = "medium",
	label = "label"
}

export enum TextType {
	regular = "regular",
	small = "small",
	caps = "caps",
	large = "large",
	medium = "medium",
	label = "label"
}

export enum TextAlignment {
	left = "left",
	center = "center",
	right = "right"
}

interface TextProps {
	tag: string;
	id?: string;
	category?: TextCategory;
	type?: TextType;
	alignment?: TextAlignment;
	doTruncate?: boolean;
	isSection?: boolean;
	className?: string;
}

export class Text extends React.Component<PropsWithChildren<TextProps>> {
	render() {
		const {
			tag: Tag,
			category,
			type,
			alignment,
			doTruncate,
			isSection,
			children,
			className,
			...props
		} = this.props;
		const pprops = Object.assign({}, props);
		const textClassNames = classNames(
			{
				[`slds-text-${category}_${type}`]: type && category,
				[`slds-text-${category}`]: category && !type,
				"slds-truncate": doTruncate,
				[`slds-text-align_${alignment}`]: alignment,
				"slds-section-title_divider": isSection
			},
			className
		);
		return (
			<Tag {...(pprops as any)} className={textClassNames}>
				{children}
			</Tag>
		);
	}
}
