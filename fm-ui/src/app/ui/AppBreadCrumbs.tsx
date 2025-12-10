
import { Button, GlobalNavigationBar, GlobalNavigationBarRegion, Icon } from "@salesforce/design-system-react";
import { BreadCrumb as SldsBreadCrumb, BreadCrumbs as SldsBreadCrumbs } from "@zeitwert/ui-slds";
import React from "react";

export interface BreadCrumb {
	title: string;
	iconCategory?: string;
	iconName?: string;
}

export interface AppBreadCrumbsProps {
	items: BreadCrumb[];
	isActionProcessing: boolean;
	onPrimaryAction: () => void;
	onSecondaryAction: () => void;
	onItemClick?: (num: number) => void;
}

export default class AppBreadCrumbs extends React.Component<AppBreadCrumbsProps> {
	render() {
		const { items, isActionProcessing, onPrimaryAction, onSecondaryAction, onItemClick } = this.props;
		return (
			<GlobalNavigationBar className="slds-grid_vertical-align-center">
				<GlobalNavigationBarRegion region="primary">
					<SldsBreadCrumbs>
						<>
							{items.map((item, index) => (
								<SldsBreadCrumb
									key={index}
									onClick={() => onItemClick && onItemClick(items.length - index - 1)}
								>
									{item.iconCategory && item.iconName && (
										<Icon
											category={item.iconCategory as any}
											name={item.iconName}
											size="small"
											containerClassName="slds-m-right_x-small"
										/>
									)}
									{item.title}
								</SldsBreadCrumb>
							))}
						</>
					</SldsBreadCrumbs>
				</GlobalNavigationBarRegion>
				<GlobalNavigationBarRegion region="tertiary">
					<div className="slds-text-align_right slds-m-right_large">
						<Button key="cancel" label="Cancel" onClick={onSecondaryAction} disabled={isActionProcessing} />
						<Button
							key="save"
							label="Save"
							variant="brand"
							onClick={onPrimaryAction}
							disabled={isActionProcessing}
						/>
					</div>
				</GlobalNavigationBarRegion>
			</GlobalNavigationBar>
		);
	}
}
