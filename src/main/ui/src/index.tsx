
import { IconSettings } from "@salesforce/design-system-react";
import {
	AppStoreModel,
	assertThis,
	Env, Locale, observeMutation, session, unregisterServiceWorker
} from "@zeitwert/ui-model";
import "assets/app.css";
import { NavigatorImpl } from "frame/app/impl/NavigationImpl";
import Highcharts from "highcharts";
import Logger from "loglevel";
import { configure } from "mobx";
import { observer, Provider } from "mobx-react";
import moment from "moment";
import "moment/locale/de-ch";
import React from "react";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App, { AppCtx } from "./frame/App";
import AuthFrame from "./frame/AuthFrame";
import NotificationFrame from "./frame/NotificationFrame";

// Lightning Modal sets body.style.overflow to inherit, revert this when it happens
observeMutation("body", {
	attributeFilter: ["style"], // Only the "style" attribute
	attributeOldValue: true,   // Report also the oldValue
}, (m) => {
	if (m.oldValue === "overflow: hidden;") {
		document.getElementsByTagName("body")[0].style.overflow = "hidden";
	}
});

// TODO: remove when implemented: https://mobx.js.org/actions.html#asynchronous-actions
configure({
	enforceActions: "never"
});

// This is important for various things (calendar included).
moment.locale(Locale.de_ch);

Highcharts.setOptions({
	lang: {
		thousandsSep: "'"
	},
	accessibility: {
		enabled: false
	}
});

const logLevel: Logger.LogLevelDesc = Env.getParam("LOG_LEVEL") as Logger.LogLevelDesc;
Logger.setLevel(logLevel);

// Navigation.
const navigator = new NavigatorImpl(session);

// Base stores.
const appStore = AppStoreModel.create({});

const appCtx: AppCtx = {
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
					<Provider {...appCtx}>
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

const rootContainer = document.getElementById("root");
assertThis(rootContainer != null);
const root = createRoot(rootContainer); // createRoot(container!) if you use TypeScript
root.render(<Frame />);

unregisterServiceWorker();
