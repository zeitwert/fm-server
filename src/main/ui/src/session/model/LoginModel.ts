
import { EnumeratedField, TextField } from "@zeitwert/ui-forms";
import { Enumerated, session, Session } from "@zeitwert/ui-model";
import { types } from "mobx-state-tree";
import { Form, Query } from "mstform";

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
		get isReadyForLogin(): boolean {
			return !!self.email && !!self.password && !!self.account;
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

const loadAccounts = async (q: Query): Promise<Enumerated[]> => {
	if (isValidEmail(q.email)) {
		const userInfoResponse = await session.userInfo(q.email!);
		if (userInfoResponse) {
			return userInfoResponse.accounts;
		}
	}
	return [];
};

const isValidEmail = (email: string | undefined): boolean => {
	return !!email && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email);
};

export const LoginData = LoginModel.create({});

export const LoginFormModel = new Form(
	LoginModel,
	{
		email: new TextField({ required: true }),
		password: new TextField({ required: true }),
		account: new EnumeratedField({
			source: loadAccounts,
			dependentQuery: (accessor) => {
				return { email: accessor.node.email };
			}
		}),
	}
);
