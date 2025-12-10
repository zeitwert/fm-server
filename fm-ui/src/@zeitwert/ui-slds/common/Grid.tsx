import classNames from "classnames";
import React, { PropsWithChildren } from "react";

interface GridProps {
	tag?: string;
	isFrame?: boolean;
	isVertical?: boolean;
	className?: string;
	style?: React.CSSProperties;
}

export class Grid extends React.Component<PropsWithChildren<GridProps>> {
	render() {
		const { className, isFrame, isVertical, children, tag, ...props } = this.props;
		const Tag = tag || "div";
		const classes = classNames(
			"slds-grid",
			isFrame ? "slds-grid_frame" : null,
			isVertical ? "slds-grid_vertical" : null,
			className
		);
		return (
			<Tag className={classes} {...(props as any)}>
				{children}
			</Tag>
		);
	}

	public static defaultProps: Partial<GridProps> = {
		isVertical: true
	};

}

export enum ColumnAlignment {
	top = "top",
	medium = "medium",
	bottom = "bottom"
}

interface ColProps {
	tag?: string;
	padding?: string; // medium, large
	alignment?: ColumnAlignment;
	noFlex?: boolean;
	order?: number;
	orderSmall?: number;
	orderMedium?: number;
	orderLarge?: number;
	cols?: number;
	totalCols?: number;
	colsSmall?: number;
	totalColsSmall?: number;
	colsMedium?: number;
	totalColsMedium?: number;
	colsLarge?: number;
	totalColsLarge?: number;
	className?: string;
	style?: React.CSSProperties;
}

export class Col extends React.Component<PropsWithChildren<ColProps>> {
	render() {
		const {
			tag,
			className,
			padding,
			alignment,
			noFlex,
			order,
			orderSmall,
			orderMedium,
			orderLarge,
			cols,
			colsSmall,
			colsMedium,
			colsLarge,
			totalCols,
			totalColsSmall,
			totalColsMedium,
			totalColsLarge,
			children,
			...pprops
		} = this.props;
		const Tag = tag || "div";
		const rowClassNames = classNames(
			className,
			padding ? `slds-col_padded${/^(medium|large)$/.test(padding) ? `-${padding}` : ""}` : "slds-col",
			alignment ? `slds-align-${alignment}` : null,
			noFlex ? "slds-no-flex" : null,
			order ? `slds-order_${order}` : null,
			orderSmall ? `slds-small-order_${orderSmall}` : null,
			orderMedium ? `slds-medium-order_${orderMedium}` : null,
			orderLarge ? `slds-large-order_${orderLarge}` : null,
			cols && totalCols ? `slds-size_${cols}-of-${this.adjustCols(totalCols, true)}` : null,
			colsSmall && totalColsSmall ? `slds-small-size_${colsSmall}-of-${this.adjustCols(totalColsSmall)}` : null,
			colsMedium && totalColsMedium
				? `slds-medium-size_${colsMedium}-of-${this.adjustCols(totalColsMedium)}`
				: null,
			colsLarge && totalColsLarge
				? `slds-large-size_${colsLarge}-of-${this.adjustCols(totalColsLarge, true)}`
				: null
		);
		return (
			<Tag className={rowClassNames} {...(pprops as React.HTMLProps<HTMLDivElement> as any)}>
				{children}
			</Tag>
		);
	}

	private adjustCols(colNum: number, large: boolean = false): number {
		if (colNum > 6) {
			return large ? 12 : 6;
		}
		return colNum;
	}
}

export enum RowAlignment {
	center = "center",
	space = "space",
	spread = "spread"
}

interface RowProps {
	alignment?: RowAlignment;
	nowrap?: boolean;
	nowrapSmall?: boolean;
	nowrapMedium?: boolean;
	nowrapLarge?: boolean;
	pullPadded?: boolean;
	cols?: number;
	colsSmall?: number;
	colsMedium?: number;
	colsLarge?: number;
	className?: string;
}

export class Row extends React.Component<PropsWithChildren<RowProps>> {
	render() {
		const {
			className,
			alignment,
			nowrap,
			nowrapSmall,
			nowrapMedium,
			nowrapLarge,
			cols,
			colsSmall,
			colsMedium,
			colsLarge,
			pullPadded,
			children,
			...props
		} = this.props;
		const rowClassNames = classNames(
			"slds-grid",
			alignment ? `slds-grid_align-${alignment}` : null,
			nowrap ? "slds-nowrap" : "slds-wrap",
			nowrapSmall ? "slds-nowrap_small" : null,
			nowrapMedium ? "slds-nowrap_medium" : null,
			nowrapLarge ? "slds-nowrap_large" : null,
			pullPadded ? "slds-grid_pull-padded" : null,
			className
		);
		const totalCols =
			cols ||
			((): number => {
				let cnt = 0;
				React.Children.forEach(children as any, (child: React.ReactChild) => {
					if (!React.isValidElement(child)) {
						return;
					}
					cnt += child.props?.["cols"] || 1;
				});
				return cnt;
			})();
		const colProps = {
			totalCols,
			totalColsSmall: colsSmall || totalCols,
			totalColsMedium: colsMedium || totalCols,
			totalColsLarge: colsLarge || totalCols
		};
		return (
			<div className={rowClassNames} {...(props as React.HTMLProps<HTMLDivElement>)}>
				{React.Children.map(children as any, this.renderColumn.bind(this, colProps))}
			</div>
		);
	}

	private renderColumn = (colProps: ColProps, child: JSX.Element) => {
		if (child.type !== Col) {
			return <Col {...colProps}>{child}</Col>;
		}
		const propAggregator = (cprops: ColProps, key: string): any => {
			cprops[key] = child.props[key] || colProps[key];
			return cprops;
		};
		const childProps = Object.keys(colProps).reduce(propAggregator, {});
		return React.cloneElement(child, childProps);
	};

}
