import { transaction } from "mobx";
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { session, UserInfo } from "../../../app/session";
import { ItemPartModel } from "../../../ddd/item/model/ItemPartModel";

const MstItemPartNoteModel = ItemPartModel.named("ItemPartNote")
	.props({
		subject: types.maybe(types.string),
		content: types.maybe(types.string),
		isPrivate: types.optional(types.boolean, false),
		//
		createdByUser: types.maybe(types.frozen<UserInfo>()),
		createdAt: types.maybe(faTypes.dateWithOffset),
		modifiedByUser: types.maybe(types.frozen<UserInfo>()),
		modifiedAt: types.maybe(faTypes.dateWithOffset)
	})
	.views((self) => ({
		isVisible(user: UserInfo) {
			return !self.isPrivate || self.createdByUser!.id === user.id;
		}
	}))
	.views((self) => ({
		canModify(user: UserInfo) {
			return self.isVisible(user);
		}
	}))
	.actions((self) => ({
		setPrivate(isPrivate: boolean) {
			self.isPrivate = !!isPrivate;
		},
		modify(note: any) {
			transaction(() => {
				self.subject = note.subject;
				self.content = note.content;
				self.isPrivate = note.isPrivate;
				self.modifiedAt = new Date();
				self.modifiedByUser = session.sessionInfo?.user;
			});
		}
	}));

type MstItemPartNoteType = typeof MstItemPartNoteModel;
export interface MstItemPartNote extends MstItemPartNoteType { }
export const ItemPartNoteModel: MstItemPartNote = MstItemPartNoteModel;
export interface ItemPartNote extends Instance<typeof ItemPartNoteModel> { }
export type MstItemPartNoteSnapshot = SnapshotIn<typeof MstItemPartNoteModel>;
export interface ItemPartNoteSnapshot extends MstItemPartNoteSnapshot { }
export type ItemPartNotePayload = Omit<ItemPartNoteSnapshot, "id">;
