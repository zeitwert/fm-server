
import { IconSettings } from "@salesforce/design-system-react";
import {
	AppStoreModel,
	Env, Locale,
	session, unregisterServiceWorker
} from "@zeitwert/ui-model";
import "assets/app.css";
import { NavigatorImpl } from "frame/app/impl/NavigationImpl";
import Logger from "loglevel";
import { configure } from "mobx";
import { observer, Provider } from "mobx-react";
import moment from "moment";
import "moment/locale/en-gb";
import React from "react";
import "react-big-calendar/lib/css/react-big-calendar.css";
import * as ReactDOM from "react-dom";
import { BrowserRouter } from "react-router-dom";
import App, { AppCtx } from "./App";
import AuthFrame from "./frame/AuthFrame";
import NotificationFrame from "./frame/NotificationFrame";

// TODO: remove when implemented: https://mobx.js.org/actions.html#asynchronous-actions
configure({
	enforceActions: "never"
});

// This is important for various things (calendar included).
moment.locale(Locale.en_gb);

const logLevel: Logger.LogLevelDesc = Env.getParam("LOG_LEVEL") as Logger.LogLevelDesc;
Logger.setLevel(logLevel);

// Navigation.
const navigator = new NavigatorImpl(session);

// Base stores.
const appStore = AppStoreModel.create({});

const store: AppCtx = {
	appStore,
	logger: Logger,
	navigator,
	session,
	showToast: () => { },
	showAlert: () => { }
};

@observer
class Frame extends React.Component {
	render() {
		return (
			<BrowserRouter>
				<IconSettings iconPath="/assets/icons">
					<Provider {...store}>
						<NotificationFrame>
							<AuthFrame>
								<App isInit={session.isInit} />
							</AuthFrame>
						</NotificationFrame>
					</Provider>
				</IconSettings>
			</BrowserRouter>
		);
	}
}

ReactDOM.render(<Frame />, document.getElementById("root"));

unregisterServiceWorker();
