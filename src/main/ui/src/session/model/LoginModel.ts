
import { Enumerated, Session } from "@zeitwert/ui-model";
import { types } from "mobx-state-tree";

export const isValidEmail = (email: string | undefined): boolean => {
	return !!email && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email);
};

export const LoginModel = types
	.model("Login", {
		email: types.maybe(types.string),
		password: types.maybe(types.string),
		account: types.maybe(types.frozen<Enumerated>()),
	})
	.views((self) => ({
		get isValidEmail(): boolean {
			return isValidEmail(self.email);
		},
	}))
	.views((self) => ({
		get isReadyForLogin(): boolean {
			return isValidEmail(self.email) && !!self.password && !!self.account;
		}
	}))
	.actions((self) => ({
		async login(session: Session) {
			if (self.isReadyForLogin) {
				await session.login(self.email!, self.password!, self.account);
				if (!session.isAuthenticated) {
					alert("Could not log in!");
				}
			}
		}
	}));

export const LoginData = LoginModel.create({});
