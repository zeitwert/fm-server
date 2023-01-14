import { Instance, SnapshotIn, types } from "mobx-state-tree";
import moment from "moment";
import { Enumerated } from "../../../../ui-model/ddd/aggregate/model/EnumeratedModel";
import { faTypes } from "../../../app/common";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";

const MstAnniversaryModel = ObjPartModel.named("Anniversary")
	.props({
		anniversaryTypeId: types.maybe(types.frozen<Enumerated>()),
		startDate: types.maybe(faTypes.date),
		anniversaryNotificationId: types.maybe(types.frozen<Enumerated>()),
		anniversaryTemplateId: types.maybe(types.frozen<Enumerated>())
	})
	.views((self) => ({
		get isUpcoming() {
			return moment(self.startDate).diff(moment()) > 0;
		},
		get isOverdue() {
			return !this.isUpcoming;
		}
	}));

type MstAnniversaryType = typeof MstAnniversaryModel;
interface MstAnniversary extends MstAnniversaryType { }

export const AnniversaryModel: MstAnniversary = MstAnniversaryModel;
export type AnniversaryModelType = typeof AnniversaryModel;
export interface Anniversary extends Instance<AnniversaryModelType> { }
export type AnniversarySnapshot = SnapshotIn<AnniversaryModelType>;
export type AnniversaryPayload = Omit<AnniversarySnapshot, "id">;
