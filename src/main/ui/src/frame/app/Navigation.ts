import { Navigation, NavigationAction, NavigationTarget } from "@zeitwert/ui-model";

export interface Navigator {
	readonly params: any;

	navigate(areaId: string, navigation: Navigation): string;

	navigateTo(areaId: string, target: NavigationTarget, action: NavigationAction): string;
}
