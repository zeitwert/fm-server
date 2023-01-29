
import { Language, Locale, Session, SessionModel } from "@zeitwert/ui-model";
import App from "app/App";
import { createBrowserHistory } from "history";
import { observer, Provider } from "mobx-react";
import React from "react";
import * as ReactDOM from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";

const history = createBrowserHistory();

const session: Session = SessionModel.create({
	locale: Language.en,
	sessionInfo: {
		applicationName: "zeitwert-server",
		applicationVersion: "1.0.0",
		tenant: {
			id: "1",
			caption: "Tenant1",
			name: "Tenant1",
			extlKey: "T1",
			tenantType: { id: "kernel", name: "Kernel" },
			logo: undefined
		},
		user: {
			id: "hob",
			caption: "Hannes Brunner",
			name: "Hannes Brunner",
			tenant: { id: "1", name: "Tenant1" },
			email: "hannes_brunner@hotmail.com",
			role: { id: "admin", name: "Admin" },
		},
		locale: Locale.en_us,
		applicationId: "fm",
		applications: []
	},
	appInfo: {
		id: "advice",
		name: "zeitwert:advice",
		areas: [],
		defaultArea: ""
	}
});

const store = {
	history,
	session
};

const AuthenticatedApp: React.FunctionComponent<{}> = (props: any) => <>{session.isAuthenticated && <App isInit={true} />}</>;

const ObservedAuthenticatedApp = observer(AuthenticatedApp);

it("renders without crashing", () => {
	const div = document.createElement("div");
	ReactDOM.render(
		<Provider {...store}>
			<BrowserRouter>
				<Routes>
					<Route element={<ObservedAuthenticatedApp />} />
				</Routes>
			</BrowserRouter>
		</Provider>,
		div
	);
});
