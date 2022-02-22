
import { EnumeratedField, TextField } from "@comunas/ui-forms";
import { Enumerated, session, Session } from "@comunas/ui-model";
import { types } from "mobx-state-tree";
import { Form, Query } from "mstform";

export const LoginModel = types
	.model("Login", {
		email: types.maybe(types.string),
		password: types.maybe(types.string),
		community: types.maybe(types.frozen<Enumerated>()),
	})
	.views((self) => ({
		get isValidEmail(): boolean {
			return isValidEmail(self.email);
		},
		get isReadyForLogin(): boolean {
			return !!self.email && !!self.password && !!self.community;
		}
	}))
	.actions((self) => ({
		async login(session: Session) {
			if (self.isReadyForLogin) {
				await session.login(self.email!, self.password!, self.community);
				if (!session.isAuthenticated) {
					alert("Could not log in!");
				}
			}
		}
	}));

const loadCommunities = async (q: Query): Promise<Enumerated[]> => {
	if (isValidEmail(q.email)) {
		const userInfoResponse = await session.userInfo(q.email!);
		if (userInfoResponse) {
			return userInfoResponse.communities;
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
		community: new EnumeratedField({
			source: loadCommunities,
			dependentQuery: (accessor) => {
				return { email: accessor.node.email };
			}
		}),
	}
);
