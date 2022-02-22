import { BreadCrumb as BreadCrumbItem } from "@comunas/ui-model";
import { BreadCrumb, BreadCrumbs } from "@comunas/ui-slds/common/BreadCrumbs";
import Button from "@salesforce/design-system-react/components/button";
import GlobalNavigationBar from "@salesforce/design-system-react/components/global-navigation-bar";
import GlobalNavigationBarRegion from "@salesforce/design-system-react/components/global-navigation-bar/region";
import Icon from "@salesforce/design-system-react/components/icon";
import React from "react";

interface AppBreadCrumbsProps {
	items: BreadCrumbItem[];
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
					<BreadCrumbs>
						<>
							{items.map((item, index) => (
								<BreadCrumb
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
								</BreadCrumb>
							))}
						</>
					</BreadCrumbs>
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
