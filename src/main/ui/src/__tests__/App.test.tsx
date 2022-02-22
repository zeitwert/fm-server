import { Language, Locale, Session, SessionModel } from "@comunas/ui-model";
import App from "App";
import { createBrowserHistory } from "history";
import { observer, Provider } from "mobx-react";
import React from "react";
import * as ReactDOM from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";

const history = createBrowserHistory();

const session: Session = SessionModel.create({
	locale: Language.en,
	sessionInfo: {
		tenant: {
			id: "1",
			name: "Tenant1",
			extlKey: "t1"
		},
		user: {
			id: "hob",
			name: "Hannes Brunner",
			emailProvider: { id: "exchange" },
			email: "hannes_brunner@hotmail.com"
		},
		locale: Locale.en_us,
		applicationId: "advise",
		applications: []
	},
	appInfo: {
		id: "advice",
		name: "finadvise:advice",
		areas: [],
		defaultArea: ""
	}
});

const store = {
	history,
	session
};

const AuthenticatedApp: React.FunctionComponent<{}> = (props: any) => <>{session.isAuthenticated && <App />}</>;

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
