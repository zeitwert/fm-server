import {
	Navigation,
	NavigationAction,
	NavigationTarget,
	ROUTE_ACTION,
	SELF_TARGET,
	Session
} from "@zeitwert/ui-model";
import { Navigator } from "app/frame/Navigation";

export class NavigatorImpl implements Navigator {
	session: Session;
	private _params: any;

	get params() {
		return this._params;
	}

	constructor(session: Session) {
		this.session = session;
	}

	navigate(areaId: string, navigation: Navigation) {
		return this.navigateTo(areaId, navigation.target, navigation.action);
	}

	navigateTo(areaId: string, target: NavigationTarget, action: NavigationAction) {
		areaId = target.applicationAreaId === SELF_TARGET ? areaId : target.applicationAreaId;

		const areaPath = this.session!.appAreaMap![areaId].path;
		if (action.actionType === ROUTE_ACTION) {
			const targetUrl = areaPath.startsWith("/") ? areaPath : "/" + areaPath;
			this._params = action.params;
			return targetUrl;
		}
		return "/";
	}
}
