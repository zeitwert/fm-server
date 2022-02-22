import classNames from "classnames";
import React from "react";

enum PageHeaderVariant {
	OBJECT_HOME = "object-home",
	RECORD_HOME = "record-home"
}

interface ScrollablePageHeaderProps {
	variant: string;
	element?: HTMLDivElement;
	withWrapper?: boolean;
	hasDetails?: boolean;
	isDocked?: boolean;
	className?: string;
	children: (
		classes: string,
		renderDetail: boolean,
		headerStyle: React.CSSProperties,
		detailStyle: React.CSSProperties
	) => JSX.Element;
}

interface ScrollablePageHeaderState {
	hasScrollbar: boolean;
	scrollTop: number;
}

export class ScrollablePageHeader extends React.Component<ScrollablePageHeaderProps, ScrollablePageHeaderState> {
	state = {
		hasScrollbar: false,
		scrollTop: 0
	};

	componentDidMount() {
		this.props.element?.addEventListener("scroll", this.handleScroll);
		this.props.element?.addEventListener("resize", this.handleScroll);
		// TODO: after first time mounted, then needs to be recalculated.
		setTimeout(() => this.handleScroll(), 140);
	}

	componentWillUnmount() {
		this.props.element?.removeEventListener("scroll", this.handleScroll);
		this.props.element?.removeEventListener("resize", this.handleScroll);
	}

	private handleScroll = () => {
		const hasScrollbar = this.props.element?.scrollHeight! > this.props.element?.clientHeight!;
		const scrollTop = Math.max(this.props.element?.scrollTop || 0);
		this.setState({
			hasScrollbar: hasScrollbar,
			scrollTop: scrollTop
		});
	};

	render() {
		const withWrapper = this.props.withWrapper === undefined ? true : this.props.withWrapper;
		const wrapperClasses = classNames("page-dynamic-height", this.props.isDocked ? "page-docked" : null);
		const classes = classNames(
			!withWrapper ? wrapperClasses : "",
			"slds-page-header slds-shrink-none slds-page-header_joined",
			this.props.className ? this.props.className : null
		);

		let minHeaderHeight = 0,
			maxHeaderHeight = 0,
			maxDetailHeight = 0;
		const maxHeaderMargin = this.props.isDocked ? 0 : 12;
		const maxBorderRadius = this.props.isDocked ? 0 : 4;
		switch (this.props.variant) {
			default:
			case PageHeaderVariant.OBJECT_HOME:
				minHeaderHeight = 100;
				maxHeaderHeight = 100;
				break;
			case PageHeaderVariant.RECORD_HOME:
				maxDetailHeight = 85;
				minHeaderHeight = 65;
				maxHeaderHeight = 65 + (this.props.hasDetails ? maxDetailHeight : 0);
				break;
		}

		let headerStyle = {},
			detailStyle = {},
			placeholderStyle = {};
		let renderDetail = true;
		const maxScroll = maxHeaderHeight! - minHeaderHeight! + maxHeaderMargin!;
		const scrollTop = this.state.scrollTop;
		const headerHeight =
			scrollTop <= maxHeaderMargin!
				? maxHeaderHeight!
				: Math.max(minHeaderHeight!, maxHeaderHeight! + maxHeaderMargin! - scrollTop);
		const headerTransform = -Math.min(scrollTop, maxHeaderMargin!);
		const completed = 1 - Math.min(1, scrollTop / maxScroll);
		const scrollBarWidth = this.props.element?.offsetWidth! - this.props.element?.clientWidth!;
		headerStyle = {
			height: headerHeight,
			left: completed * maxHeaderMargin,
			right: completed * maxHeaderMargin + scrollBarWidth,
			marginTop: completed,
			borderRadius: completed * maxBorderRadius,
			transform: "translate3d(0px, " + headerTransform + "px, 0px)"
		};
		placeholderStyle = {
			height: "1px",
			marginTop: maxHeaderHeight + "px"
		};

		if (this.props.variant === PageHeaderVariant.RECORD_HOME) {
			const maxTopHeight = maxHeaderHeight! - maxDetailHeight + 2;
			const detailHeight = headerHeight - maxTopHeight;
			const completedOpacity = 1 - Math.min(1, (1.5 * scrollTop) / maxScroll);
			detailStyle = {
				opacity: completedOpacity,
				height: detailHeight
			};
			renderDetail = detailHeight > 0;
		}

		const content = () => {
			const c = this.props.children && this.props.children(classes, renderDetail, headerStyle, detailStyle);
			if (withWrapper) {
				return (
					<div key={0} className={wrapperClasses} style={headerStyle}>
						{c}
					</div>
				);
			}
			return c;
		};

		return [
			content(),
			<div
				key={1}
				className={"page-head-placeholder" + (this.props.className ? " " + this.props.className : "")}
				style={placeholderStyle}
			/>
		];
	}
}
