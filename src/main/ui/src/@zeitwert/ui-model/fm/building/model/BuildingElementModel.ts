
import { BuildingModel, Enumerated } from "@zeitwert/ui-model";
import { toJS } from "mobx";
import { getParentOfType, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";

export const LowOption: Enumerated = { id: "-1", name: "Gering", itemType: undefined };
export const NormalOption: Enumerated = { id: "0", name: "-", itemType: undefined };
export const HighOption: Enumerated = { id: "1", name: "Hoch", itemType: undefined };

export const StrengthOptions: Enumerated[] = [LowOption, NormalOption, HighOption];
export const StrainOptions: Enumerated[] = [HighOption, NormalOption, LowOption];

export const ShortTermYears: number = 1;
export const MidTermYears: number = 5;

const MstBuildingElementModel = ObjPartModel.named("BuildingElement")
	.props({
		buildingPart: types.maybe(types.frozen<Enumerated>()),
		valuePart: types.maybe(types.number),
		condition: types.maybe(types.number),
		conditionYear: types.maybe(types.number),
		strain: types.maybe(types.number),
		strength: types.maybe(types.number),
		description: types.maybe(types.string),
		conditionDescription: types.maybe(types.string),
		measureDescription: types.maybe(types.string),
		materialDescriptions: types.optional(types.array(types.frozen<Enumerated>()), []),
		conditionDescriptions: types.optional(types.array(types.frozen<Enumerated>()), []),
		measureDescriptions: types.optional(types.array(types.frozen<Enumerated>()), []),
		// calculated fields from server
		restorationYear: types.maybe(types.number),
		restorationCosts: types.maybe(types.number),
		lifeTime20: types.number,
		lifeTime50: types.number,
		lifeTime70: types.number,
		lifeTime85: types.number,
		lifeTime95: types.number,
		lifeTime100: types.number,
	})
	.views((self) => ({
		get isValidBuilding(): boolean {
			const building = getParentOfType(self, BuildingModel);
			return !!building.insuredValue && !!building.insuredValueYear && !!building.buildingPartCatalog && !!building.buildingMaintenanceStrategy;
		},
	}))
	.views((self) => ({
		get isValidElement(): boolean {
			return self.isValidBuilding && !!self.valuePart && !!self.condition && !!self.conditionYear;
		},
	}))
	.views((self) => {
		const thisYear = (new Date()).getFullYear();
		return {
			get restorationAge(): number | undefined {
				return self.restorationYear ? self.restorationYear - thisYear : undefined;
			},
			get shortTermRestoration(): number | undefined {
				const relativeAge = self.restorationYear ? self.restorationYear - thisYear : undefined;
				return relativeAge && relativeAge <= ShortTermYears ? self.restorationYear : undefined;
			},
			get midTermRestoration(): number | undefined {
				const relativeAge = self.restorationYear ? self.restorationYear - thisYear : undefined;
				return relativeAge && ShortTermYears < relativeAge && relativeAge <= MidTermYears ? self.restorationYear : undefined;
			},
			get longTermRestoration(): number | undefined {
				const relativeAge = self.restorationYear ? self.restorationYear - thisYear : undefined;
				return relativeAge && relativeAge > MidTermYears ? self.restorationYear : undefined;
			}
		}
	})
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				id: !self.id.startsWith("New:") ? self.id : undefined
			});
		},
	}));

type MstBuildingElementType = typeof MstBuildingElementModel;
export interface MstBuildingElement extends MstBuildingElementType { }
export const BuildingElementModel: MstBuildingElement = MstBuildingElementModel;
export interface BuildingElement extends Instance<typeof BuildingElementModel> { }
export type MstBuildingElementSnapshot = SnapshotIn<typeof MstBuildingElementModel>;
export interface BuildingElementSnapshot extends MstBuildingElementSnapshot { }
export type BuildingElementPayload = Omit<BuildingElementSnapshot, "id">;
