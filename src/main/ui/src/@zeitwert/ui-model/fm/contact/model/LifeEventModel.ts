import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import moment from "moment";
import { faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";

const MstLifeEventModel = ObjPartModel.named("LifeEvent")
	.props({
		lifeEventTypeId: types.maybe(types.frozen<Enumerated>()),
		name: types.maybe(types.string),
		objId: types.maybe(types.number),
		description: types.maybe(types.string),
		startDate: types.maybe(faTypes.date),
		lifeEventNotificationId: types.maybe(types.frozen<Enumerated>()),
		lifeEventTemplateId: types.maybe(types.frozen<Enumerated>()),
		lifeEventTemplate: types.maybe(types.frozen<Enumerated>()),
		isDeterministic: types.maybe(types.boolean),
		isOwn: types.maybe(types.boolean),
		isGoal: types.maybe(types.boolean)
	})
	.views((self) => ({
		get isFuture() {
			return moment(self.startDate).diff(moment()) > 0;
		},
		get isPast() {
			return !this.isFuture;
		},
		get isRemindMe() {
			return self.lifeEventTemplateId?.id.startsWith("rem");
		}
	}))
	.actions((self) => ({
		setLifeEventBirthDateName(childFirstName: string) {
			if (self.name === "Birth Date") {
				self.name = childFirstName + "'s " + self.name;
				self.isOwn = false;
			}
		},
		setLifeEventLegalMajorityAge(childFirstName: string) {
			if (self.name === "Legal Majority Age") {
				self.name = childFirstName + "'s " + self.name;
				self.isOwn = false;
			}
		},
		setLifeEventFinancialIndependenceAge(childFirstName: string) {
			if (self.name === "Financial Independence Age") {
				self.name = childFirstName + "'s " + self.name;
				self.isOwn = false;
			}
		},
		setLifeEventAccountName(firstName: string) {
			if (!self.name?.startsWith(firstName)) {
				self.name = firstName + "'s " + self.name;
			}
		}
	}))
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				id: !self.id?.startsWith("New:") ? self.id : undefined
			});
		}
	}));

type MstLifeEventType = typeof MstLifeEventModel;
export interface MstLifeEvent extends MstLifeEventType { }

export const LifeEventModel: MstLifeEvent = MstLifeEventModel;
export type LifeEventModelType = typeof LifeEventModel;
export interface LifeEvent extends Instance<LifeEventModelType> { }
export type LifeEventSnapshot = SnapshotIn<LifeEventModelType>;
export type LifeEventPayload = Omit<LifeEventSnapshot, "id">;
