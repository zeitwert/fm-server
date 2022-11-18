
import { Config } from "@zeitwert/ui-model/app";
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { DocumentModel } from "../../dms/model/DocumentModel";

export interface UserStatistics {
}

const MstUserModel = ObjModel.named("User")
	.props({
		email: types.maybe(types.string),
		password: types.maybe(types.string),
		role: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		//
		avatar: types.maybe(types.reference(DocumentModel)),
	})
	.views((self) => ({
		get hasAvatar(): boolean {
			return !!self.avatar?.id && !!self.avatar?.contentType?.id;
		},
		get avatarUrl(): string | undefined {
			if (self.avatar?.id && self.avatar?.contentType?.id) {
				return Config.getRestUrl("dms", "documents/" + self.avatar?.id + "/content");
			}
			return "/missing.jpg";
		},
	}))
	.views((self) => ({
		get formSnapshot(): UserSnapshot {
			return toJS(getSnapshot(self));
		}
	}));

type MstUserType = typeof MstUserModel;
export interface MstUser extends MstUserType { }
export const UserModel: MstUser = MstUserModel;
export interface User extends Instance<typeof UserModel> { }
export type UserSnapshot = SnapshotIn<typeof MstUserModel>;
export type UserPayload = Omit<UserSnapshot, "id">;
