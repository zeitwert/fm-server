
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../../ui-model/ddd/aggregate/model/EnumeratedModel";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";

const MstAddressModel = ObjPartModel.named("Address")
	.props({
		name: types.maybe(types.string),
		//
		street: types.maybe(types.string),
		zip: types.maybe(types.string),
		city: types.maybe(types.string),
		state: types.maybe(types.string),
		country: types.maybe(types.frozen<Enumerated>()),
		//
		channel: types.maybe(types.frozen<Enumerated>()),
		isFavorite: types.maybe(types.boolean),
		isPostalAddress: types.maybe(types.boolean)
	});

type MstAddressType = typeof MstAddressModel;
interface MstAddress extends MstAddressType { }

export const AddressModel: MstAddress = MstAddressModel;
export type AddressModelType = typeof AddressModel;
export interface Address extends Instance<AddressModelType> { }
export type AddressSnapshot = SnapshotIn<AddressModelType>;
export type AddressPayload = Omit<AddressSnapshot, "id">;
