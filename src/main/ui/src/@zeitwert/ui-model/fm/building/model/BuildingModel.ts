
import { toJS } from "mobx";
import { flow, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Config } from "../../../app/common/config/Config";
import { API } from "../../../app/common/service/Api";
import { UUID } from "../../../app/common/utils/Id";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { BuildingElement, BuildingElementModel } from "./BuildingElementModel";
import { BuildingStore } from "./BuildingStore";

const MstBuildingModel = ObjModel.named("Building")
	.props({
		account: types.maybe(types.reference(AccountModel)),
		//
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		buildingNr: types.maybe(types.string),
		buildingInsuranceNr: types.maybe(types.string),
		plotNr: types.maybe(types.string),
		nationalBuildingId: types.maybe(types.string),
		historicPreservation: types.maybe(types.frozen<Enumerated>()),
		//
		buildingType: types.maybe(types.frozen<Enumerated>()),
		buildingSubType: types.maybe(types.frozen<Enumerated>()),
		buildingPartCatalog: types.maybe(types.frozen<Enumerated>()),
		buildingYear: types.maybe(types.number),
		currency: types.maybe(types.frozen<Enumerated>()),
		//
		street: types.maybe(types.string),
		zip: types.maybe(types.string),
		city: types.maybe(types.string),
		country: types.maybe(types.frozen<Enumerated>()),
		//
		volume: types.maybe(types.number),
		areaGross: types.maybe(types.number),
		areaNet: types.maybe(types.number),
		nrOfFloorsAboveGround: types.maybe(types.number),
		nrOfFloorsBelowGround: types.maybe(types.number),
		//
		buildingMaintenanceStrategy: types.maybe(types.frozen<Enumerated>()),
		insuredValue: types.maybe(types.number),
		insuredValueYear: types.maybe(types.number),
		notInsuredValue: types.maybe(types.number),
		notInsuredValueYear: types.maybe(types.number),
		thirdPartyValue: types.maybe(types.number),
		thirdPartyValueYear: types.maybe(types.number),
		//
		elements: types.optional(types.array(BuildingElementModel), []),
	})
	.actions((self) => ({
		addElement(element: BuildingElement) {
			self.elements.push(Object.assign({}, element, { id: "New:" + UUID() }));
		},
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setAccount(id: string) {
			id && (await (self.rootStore as BuildingStore).accountsStore.loadAccount(id));
			return superSetField("account", id);
		}
		async function setBuildingPartCatalog(catalog: Enumerated) {
			self.elements.clear();
			if (!catalog?.id) {
				return;
			}
			return flow(function* () {
				try {
					const data = yield API.get(Config.getEnumUrl("building", "codeBuildingPartCatalog/" + catalog.id));
					data.data.forEach((partWeight: any) => {
						const element = {
							buildingPart: partWeight.part,
							valuePart: partWeight.weight,
							lifeTime20: partWeight.lifeTime20,
							lifeTime50: partWeight.lifeTime50,
							lifeTime70: partWeight.lifeTime70,
							lifeTime85: partWeight.lifeTime85,
							lifeTime95: partWeight.lifeTime95,
							lifeTime100: partWeight.lifeTime100,
						}
						self.addElement(element as any);
					});
					return superSetField("buildingPartCatalog", catalog);
				} catch (error: any) {
					console.error("Failed to set buildingPartCatalog", error);
				}
			})();
		}
		async function setField(field: string, value: any) {
			switch (field) {
				case "account": {
					return setAccount(value);
				}
				case "buildingPartCatalog": {
					return setBuildingPartCatalog(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setAccount,
			setBuildingPartCatalog,
			setField
		};
	})
	.views((self) => ({
		getElementById(id: string): BuildingElement | undefined {
			return !id ? undefined : self.elements.filter(e => e.id === id)?.[0];
		},
		get valuePartSum() {
			return self.elements.reduce((sum, element) => { return sum + (element.valuePart || 0.0); }, 0.0);
		}
	}))
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				id: !self.id.startsWith("New:") ? self.id : undefined,
				elements: self.elements.map((e) => e.apiSnapshot)
			});
		},
	}));

type MstBuildingType = typeof MstBuildingModel;
export interface MstBuilding extends MstBuildingType { }
export const BuildingModel: MstBuilding = MstBuildingModel;
export interface Building extends Instance<typeof BuildingModel> { }
export type MstBuildingSnapshot = SnapshotIn<typeof MstBuildingModel>;
export interface BuildingSnapshot extends MstBuildingSnapshot { }
export type BuildingPayload = Omit<BuildingSnapshot, "id">;
