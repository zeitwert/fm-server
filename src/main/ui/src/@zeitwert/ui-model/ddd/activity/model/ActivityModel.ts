
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import moment from "moment";
import { DateFormat, EntityType, faTypes } from "../../../app/common";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";
import { DocModel } from "../../doc/model/DocModel";

const MstActivityModel = DocModel.named("Activity")
	.props({
		date: faTypes.date,
		//
		contact: types.maybe(types.frozen<Enumerated>())
	})
	.views((self) => ({
		get isUpcoming() {
			return moment(self.date).diff(moment()) > 0;
		},
		get isPast() {
			return !this.isUpcoming && (!self.isInWork || self.isBusinessProcess);
		},
		get isOverdue() {
			return !this.isUpcoming && !this.isPast;
		}
	}))
	.views((self) => ({
		get timelineDescription() {
			const days = DateFormat.relativeTime(new Date(), self.date!);
			if (self.isBusinessProcess) {
				return (
					"The " +
					self.type.type +
					" process " +
					(self.contact ? "for " + self.contact.name + " " : " ") +
					(self.isInWork ? "is running " : "has been closed ") +
					days +
					"."
				);
			}
			switch (self.type.type) {
				case EntityType.TASK:
					if (self.isUpcoming) {
						return "You have " + days + " left to complete the " + self.type.type + ".";
					} else if (self.isOverdue) {
						return "You have missed the " + self.type.type + " " + days + ".";
					} else if (self.isPast) {
						return "You completed the " + self.type.type + " " + days + ".";
					}
					break;
				default:
					return "...";
			}
			return "...";
		}
	}));

type MstActivityType = typeof MstActivityModel;
interface MstActivity extends MstActivityType { }

export const ActivityModel: MstActivity = MstActivityModel;
export type ActivityModelType = typeof ActivityModel;
export interface Activity extends Instance<ActivityModelType> { }
export type ActivitySnapshot = SnapshotIn<ActivityModelType>;
export type ActivityPayload = Omit<ActivitySnapshot, "id">;
