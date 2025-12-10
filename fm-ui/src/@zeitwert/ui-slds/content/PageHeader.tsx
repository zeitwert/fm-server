import { MediaObject } from "@salesforce/design-system-react";
import { Col, ColumnAlignment, Grid, Text, TextCategory, TextType } from "@zeitwert/ui-slds";
import classNames from "classnames";
import React, { PropsWithChildren } from "react";
import { ScrollablePageHeader } from "./ScrollablePageHeader";

interface PageHeaderProps {
	className?: string;
	isObject?: boolean;
	isDocked?: boolean;
	hasDockedContent?: boolean;
	icon?: JSX.Element;
	breadCrumbs?: JSX.Element; // either breadCrumbs or legend
	legend?: string;
	title?: any;
	titleActions?: JSX.Element;
	topActions?: JSX.Element;
	details?: any[];
	info?: string;
	bottomActions?: JSX.Element;
	hasDynamicHeight?: boolean;
}

/**
 * This component is here for two reasons:
 * - Retrocompatibility with some code that is still there.
 * - For variant record-home, currently there is no possibility without changing the whole component (details).
 */
export class PageHeader extends React.Component<PropsWithChildren<PageHeaderProps>> {
	render() {
		const { isDocked, icon, info, details, topActions, bottomActions } = this.props;
		const classes = classNames(
			"slds-page-header slds-shrink-none",
			this.props.isObject ? "slds-page-header_object-home" : null,
			this.props.className ? this.props.className : null
		);

		const content = (
			classes: string,
			renderDetail: boolean,
			headerStyle: React.CSSProperties,
			detailStyle: React.CSSProperties
		) => {
			return (
				<div key={0} className={classes} style={headerStyle} role="banner">
					<div className="slds-page-header__row">
						<div className="slds-page-header__col-title">
							<MediaObject figure={icon} body={this.renderHeading()} />
						</div>
						<div className="slds-page-header__col-actions">
							<div className="slds-page-header__controls">{topActions}</div>
						</div>
					</div>
					<Grid isVertical={false}>
						<Col alignment={ColumnAlignment.bottom}>
							<Text tag="div" category={TextCategory.body} type={TextType.small}>
								{info && typeof info === "string" ? <span>{info}</span> : info}
							</Text>
						</Col>
						<Col alignment={ColumnAlignment.bottom} noFlex>
							{bottomActions}
						</Col>
					</Grid>
					{!!details?.length && renderDetail && this.renderDetails(detailStyle)}
				</div>
			);
		};

		if (this.props.hasDynamicHeight) {
			return (
				<ScrollablePageHeader
					variant={this.props.isObject ? "object-home" : "record-home"}
					element={document.getElementsByClassName("slds-brand-band").item(0) as HTMLDivElement}
					isDocked={isDocked}
					withWrapper={false}
					hasDetails={details && !!details.length}
				>
					{content}
				</ScrollablePageHeader>
			);
		}

		return content(classes, true, {}, {});
	}

	private renderHeading() {
		const { breadCrumbs, legend, title, titleActions } = this.props;
		const titlePart =
			typeof title === "string" ? (
				<PageHeaderTitle className="slds-m-right_small">{title}</PageHeaderTitle>
			) : (
				title
			);
		return (
			<>
				{breadCrumbs}
				{legend ? (
					<Text tag="p" category={TextCategory.title} type={TextType.caps}>
						{legend}
					</Text>
				) : null}
				<Grid isVertical={false}>
					{titlePart}
					<Col className="slds-shrink-none">{titleActions}</Col>
				</Grid>
			</>
		);
	}

	private renderDetails(style: React.CSSProperties) {
		if (!this.props.details) {
			return <></>;
		}

		const details: JSX.Element[] = [];
		for (let i = 0; i < this.props.details.length; i++) {
			details.push(
				<li key={i} className="slds-page-header__detail-block">
					<div
						className={"slds-text-title " + (this.props.details[i].truncate ? "slds-truncate" : "")}
						title={this.props.details[i].label}
					>
						{this.props.details[i].label}
					</div>
					{this.props.details[i].content}
				</li>
			);
		}

		return (
			<Grid tag="ul" isVertical={false} className="slds-page-header__detail-row" style={style}>
				{details}
			</Grid>
		);
	}

}

interface PageHeaderTitleProps {
	className?: string;
}

export class PageHeaderTitle extends React.Component<PropsWithChildren<PageHeaderTitleProps>> {
	render() {
		const { className, children, ...props } = this.props;
		const titleClassNames = classNames("slds-page-header__title slds-align-middle", className);
		return (
			<h1 {...(props as any)} className={titleClassNames}>
				{children}
			</h1>
		);
	}
}

interface PageHeaderDetailItemProps {
	label?: string;
	className?: string;
}

export class PageHeaderDetailItem extends React.Component<PropsWithChildren<PageHeaderDetailItemProps>> {
	render() {
		const { label, children, ...props } = this.props;
		const manuallyAssembled = !label;
		if (manuallyAssembled) {
			return (
				<li
					className={
						"slds-page-header__detail-block" + (this.props.className ? " " + this.props.className : "")
					}
					{...(props as any)}
				>
					{children}
				</li>
			);
		}

		return (
			<li
				className={"slds-page-header__detail-block" + (this.props.className ? " " + this.props.className : "")}
				{...(props as any)}
			>
				<PageHeaderDetailLabel key={0}>{label}</PageHeaderDetailLabel>
				<PageHeaderDetailBody key={1}>{children}</PageHeaderDetailBody>
			</li>
		);
	}
}

export class PageHeaderDetailLabel extends React.Component<PropsWithChildren<any>> {
	render() {
		const { children /*, ...props*/ } = this.props; // TODO
		if (typeof children === "string") {
			return (
				<Text
					tag="p"
					category={TextCategory.title}
					doTruncate
					className="slds-m-bottom_xx-small" /*{...props}*/
				>
					{children}
				</Text>
			);
		}

		return children;
	}
}

export class PageHeaderDetailBody extends React.Component<PropsWithChildren<any>> {
	render() {
		const { children /*, ...props*/ } = this.props; // TODO
		if (typeof children === "string") {
			return (
				<Text
					tag="p"
					category={TextCategory.body}
					type={TextType.regular}
					doTruncate
					className="slds-m-bottom_xx-small" /*{...props}*/
				>
					{children}
				</Text>
			);
		}

		return children;
	}
}
