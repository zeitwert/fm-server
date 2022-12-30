import Logger from "loglevel";
import { transaction } from "mobx";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { UserInfo } from "../../../app/session";
import { StoreWithContactsModel } from "../../../fm/contact/model/StoreWithContacts";
import { Aggregate } from "../../aggregate/model/AggregateModel";
import { DocStoreModel } from "../../doc/model/DocStore";
import { ACTIVITY_API } from "../service/ActivityApi";
import { ActivityModel, ActivitySnapshot } from "./ActivityModel";

const MstActivityStoreModel = DocStoreModel.named("ActivityStore")
	.props({
		contactsStore: types.optional(StoreWithContactsModel, {}),
		activities: types.optional(types.array(ActivityModel), [])
	})
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.contactsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		loadActivitiesByUser(user: UserInfo) {
			return flow(function* () {
				try {
					const repository = yield ACTIVITY_API.findByUser(user);
					if (repository.activity) {
						transaction(() => {
							self.activities.clear();
							for (const value of Object.entries(repository.activity)) {
								self.activities.push(ActivityModel.create(value[1] as ActivitySnapshot));
							}
							self.activities.sort((a1, a2) => (a1.date!.getTime() < a2.date!.getTime() ? 1 : -1));
							self.afterLoad(repository);
						});
					}
				} catch (error: any) {
					Logger.error("Failed to get activities by user", error);
				}
			})();
		},
		loadActivitiesByItem(item: Aggregate) {
			return flow(function* () {
				try {
					const repository = yield ACTIVITY_API.findByItem(item);
					if (repository.activity) {
						transaction(() => {
							self.activities.clear();
							for (const value of Object.entries(repository.activity)) {
								self.activities.push(ActivityModel.create(value[1] as ActivitySnapshot));
							}
							self.activities.sort((a1, a2) => (a1.date!.getTime() < a2.date!.getTime() ? 1 : -1));
							self.afterLoad(repository);
						});
					}
				} catch (error: any) {
					Logger.error("Failed to get activities by item", error);
				}
			})();
		}
	}));

type MstActivityStoreType = typeof MstActivityStoreModel;
export interface MstActivityStore extends MstActivityStoreType { }

export const ActivityStoreModel: MstActivityStore = MstActivityStoreModel;
export type ActivityStoreModelType = typeof ActivityStoreModel;
export interface ActivityStore extends Instance<ActivityStoreModelType> { }
export type ActivityStoreSnapshot = SnapshotIn<ActivityStoreModelType>;
export type ActivityStorePayload = Omit<ActivityStoreSnapshot, "id">;
