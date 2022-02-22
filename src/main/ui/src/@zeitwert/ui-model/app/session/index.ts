import { Language } from "../common/i18n/Language";
import { SessionModel } from "./model/SessionModel";

export * from "./model/Application";
export * from "./model/AppStoreModel";
export * from "./model/SessionInfo";
export * from "./model/SessionModel";

export const session = SessionModel.create({
	locale: Language.en
});
