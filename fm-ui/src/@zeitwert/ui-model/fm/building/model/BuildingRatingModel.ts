
import { getParent, Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";
import { BuildingElement, BuildingElementModel } from "./BuildingElementModel";

const MstBuildingRatingModel = ObjPartModel.named("BuildingRating")
	.props({
		seqNr: types.maybe(types.number),
		partCatalog: types.maybe(types.frozen<Enumerated>()),
		maintenanceStrategy: types.maybe(types.frozen<Enumerated>()),
		ratingStatus: types.maybe(types.frozen<Enumerated>()),
		ratingDate: types.maybe(faTypes.date),
		ratingUser: types.maybe(types.frozen<Enumerated>()),
		elements: types.optional(types.array(BuildingElementModel), []),
	})
	.actions((self) => ({
		addElement(element: BuildingElement) {
			self.elements.push(element);
		},
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setPartCatalog(catalog: Enumerated | undefined) {
			superSetField("partCatalog", catalog);
			self.elements.clear();
			if (!!catalog) {
				const building = getParent(self, 1) as any;
				await building.calcOnServer();
			}
		}
		function setField(field: string, value: any) {
			switch (field) {
				case "partCatalog": {
					return setPartCatalog(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setPartCatalog,
			setField
		};
	})
	.views((self) => ({
		getElementById(id: string): BuildingElement | undefined {
			return !id ? undefined : self.elements.filter(e => e.id === id)?.[0];
		},
		get weightSum() {
			return self.elements.reduce((sum, element) => { return sum + (element.weight || 0.0); }, 0.0);
		}
	}));

type MstBuildingRatingType = typeof MstBuildingRatingModel;
interface MstBuildingRating extends MstBuildingRatingType { }

export const BuildingRatingModel: MstBuildingRating = MstBuildingRatingModel;
export type BuildingRatingModelType = typeof BuildingRatingModel;
export interface BuildingRating extends Instance<BuildingRatingModelType> { }
export type BuildingRatingSnapshot = SnapshotIn<BuildingRatingModelType>;
export type BuildingRatingPayload = Omit<BuildingRatingSnapshot, "id">;
