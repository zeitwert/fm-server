
import { Optional, requireThis } from "@zeitwert/ui-model";
import { Instance, SnapshotIn, types } from "mobx-state-tree";

export interface CodeItemType {
	id: string;
	name: string;
}

export function isEnumerated(object: any): object is Enumerated {
	return object === undefined || ("id" in object && "name" in object);
}

export function canBeEnumerated(object: any): boolean {
	return object === undefined || ("id" in object && ("name" in object || "caption" in object));
}

export function asEnumerated(object: any): Enumerated | undefined {
	requireThis(canBeEnumerated(object), "object can be Enumerated");
	return !!object ? { id: object.id.toString(), name: object.caption ?? object.name } : undefined;
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
