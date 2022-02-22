import classNames from "classnames";
import React from "react";

interface BreadCrumbsProps {
	label?: string;
	className?: string;
	children: JSX.Element | JSX.Element[] | undefined;
}

export class BreadCrumbs extends React.Component<BreadCrumbsProps> {
	render() {
		const { label, className, children, ...props } = this.props;
		const olProps = props as React.OlHTMLAttributes<HTMLOListElement>;
		const olClasses = classNames("slds-breadcrumb slds-list_horizontal", className);

		return (
			<nav {...olProps} role="navigation">
				{label ? (
					<p id="bread-crumb-label" className="slds-assistive-text">
						{label}
					</p>
				) : null}
				<ol className={olClasses} aria-labelledby="bread-crumb-label">
					{children}
				</ol>
			</nav>
		);
	}
}

interface BreadCrumbProps {
	href?: string;
	className?: string;
	children?: any;
	onClick?: () => void;
}

export class BreadCrumb extends React.Component<BreadCrumbProps> {
	render() {
		const { className, href, children: text, onClick, ...props } = this.props;
		const liProps = props as React.LiHTMLAttributes<HTMLLIElement>;
		const liClasses = classNames("slds-breadcrumb__item", className);
		if (!!href) {
			return (
				<li {...liProps} className={liClasses}>
					<a
						href={href}
						onClick={(e) => {
							e.preventDefault();
							onClick && onClick();
						}}
					>
						{text}
					</a>
				</li>
			);
		} else {
			return (
				<li {...liProps} className={liClasses}>
					<span className="slds-p-around_x-small">{text}</span>
				</li>
			);
		}
	}
}
