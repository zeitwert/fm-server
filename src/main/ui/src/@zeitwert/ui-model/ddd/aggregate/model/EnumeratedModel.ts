
import { Optional } from "@zeitwert/ui-model";
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
interface MstEnumerated extends MstEnumeratedType { }

export const EnumeratedModel: MstEnumerated = MstEnumeratedModel;
export type EnumeratedModelType = typeof EnumeratedModel;
export interface Enumerated extends Optional<Instance<EnumeratedModelType>, "itemType"> { }
export type EnumeratedSnapshot = SnapshotIn<EnumeratedModelType>;
export type EnumeratedPayload = Omit<EnumeratedSnapshot, "id">;
