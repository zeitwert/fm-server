
import { Optional } from "@comunas/ui-model";
import { Instance, SnapshotIn, types } from "mobx-state-tree";

export interface CodeItemType {
	id: string;
	name: string;
}

const MstEnumeratedModel = types.model(
	"Enumerated",
	{
		id: types.identifier,
		name: types.string,
		itemType: types.maybe(types.frozen<CodeItemType>())
	});

type MstEnumeratedType = typeof MstEnumeratedModel;
export interface MstEnumerated extends MstEnumeratedType { }
export const EnumeratedModel: MstEnumerated = MstEnumeratedModel;
export interface Enumerated extends Optional<Instance<typeof EnumeratedModel>, "itemType"> { }
export type MstEnumeratedSnapshot = SnapshotIn<typeof MstEnumeratedModel>;
export interface EnumeratedSnapshot extends MstEnumeratedSnapshot { }
export type EnumeratedPayload = Omit<EnumeratedSnapshot, "id">;
