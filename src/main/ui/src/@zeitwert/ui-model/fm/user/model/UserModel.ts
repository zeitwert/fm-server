
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Config } from "../../../../ui-model/app/common/config/Config";
import { Enumerated, EnumeratedModel } from "../../../../ui-model/ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { DocumentModel } from "../../dms/model/DocumentModel";

export interface UserStatistics {
}

const MstUserModel = ObjModel.named("User")
	.props({
		email: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		//
		role: types.maybe(types.frozen<Enumerated>()),
		tenants: types.optional(types.array(EnumeratedModel), []),
		//
		avatar: types.maybe(types.reference(DocumentModel)),
		//
		password: types.maybe(types.string),
		needPasswordChange: types.maybe(types.boolean),
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
	.actions((self) => ({
		addTenant(tenant: Enumerated) {
			self.tenants.push(tenant);
		},
		removeTenant(id: string) {
			const index = self.tenants.findIndex((t) => t.id === id);
			self.tenants.splice(index, 1);
		}
	}));

type MstUserType = typeof MstUserModel;
interface MstUser extends MstUserType { }

export const UserModel: MstUser = MstUserModel;
export type UserModelType = typeof UserModel;
export interface User extends Instance<UserModelType> { }
export type UserSnapshot = SnapshotIn<UserModelType>;
export type UserPayload = Omit<UserSnapshot, "id">;
