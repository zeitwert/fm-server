import { Enumerated } from "@zeitwert/ui-model";
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";

const MstAddressModel = ObjPartModel.named("Address")
	.props({
		name: types.maybe(types.string),
		//
		street: types.maybe(types.string),
		zip: types.maybe(types.string),
		city: types.maybe(types.string),
		state: types.maybe(types.string),
		countryId: types.maybe(types.frozen<Enumerated>()),
		//
		channelId: types.maybe(types.frozen<Enumerated>()),
		isFavorite: types.maybe(types.boolean),
		isPostalAddress: types.maybe(types.boolean)
	})
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				id: !self.id?.startsWith("New:") ? self.id : undefined
			});
		}
	}));

type MstAddressType = typeof MstAddressModel;
export interface MstAddress extends MstAddressType { }
export const AddressModel: MstAddress = MstAddressModel;
export interface Address extends Instance<typeof AddressModel> { }
export type AddressSnapshot = SnapshotIn<typeof MstAddressModel>;
export type AddressPayload = Omit<AddressSnapshot, "id">;
