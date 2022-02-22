import { Navigation, NavigationAction, NavigationTarget } from "@comunas/ui-model";

export interface Navigator {
	readonly params: any;

	navigate(areaId: string, navigation: Navigation): string;

	navigateTo(areaId: string, target: NavigationTarget, action: NavigationAction): string;
}
