
import { AxiosResponse } from "axios";
import Logger from "loglevel";
import { reaction, toJS, transaction } from "mobx";
import { addDisposer, flow, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { Config } from "../../../app/common/config/Config";
import { API } from "../../../app/common/service/Api";
import { UUID } from "../../../app/common/utils/Id";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { DocumentModel } from "../../dms/model/DocumentModel";
import { BuildingElement, BuildingElementModel } from "./BuildingElementModel";
import { BuildingStore } from "./BuildingStore";
import { GeocodeRequest, GeocodeResponse } from "./GeocodeDto";

const MstBuildingModel = ObjModel.named("Building")
	.props({
		account: types.maybe(types.reference(AccountModel)),
		//
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		buildingNr: types.maybe(types.string),
		insuranceNr: types.maybe(types.string),
		plotNr: types.maybe(types.string),
		nationalBuildingId: types.maybe(types.string),
		historicPreservation: types.maybe(types.frozen<Enumerated>()),
		//
		buildingType: types.maybe(types.frozen<Enumerated>()),
		buildingSubType: types.maybe(types.frozen<Enumerated>()),
		buildingYear: types.maybe(types.number),
		currency: types.maybe(types.frozen<Enumerated>()),
		//
		street: types.maybe(types.string),
		zip: types.maybe(types.string),
		city: types.maybe(types.string),
		country: types.maybe(types.frozen<Enumerated>()),
		//
		geoAddress: types.maybe(types.string),
		geoCoordinates: types.maybe(types.string),
		geoZoom: types.maybe(types.number),
		//
		coverFoto: types.maybe(types.reference(DocumentModel)),
		//
		volume: types.maybe(types.number),
		areaGross: types.maybe(types.number),
		areaNet: types.maybe(types.number),
		nrOfFloorsAboveGround: types.maybe(types.number),
		nrOfFloorsBelowGround: types.maybe(types.number),
		//
		insuredValue: types.maybe(types.number),
		insuredValueYear: types.maybe(types.number),
		notInsuredValue: types.maybe(types.number),
		notInsuredValueYear: types.maybe(types.number),
		thirdPartyValue: types.maybe(types.number),
		thirdPartyValueYear: types.maybe(types.number),
		//
		partCatalog: types.maybe(types.frozen<Enumerated>()),
		maintenanceStrategy: types.maybe(types.frozen<Enumerated>()),
		//
		ratingStatus: types.maybe(types.frozen<Enumerated>()),
		ratingDate: types.maybe(faTypes.date),
		ratingUser: types.maybe(types.frozen<Enumerated>()),
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
		async function setBuildingPartCatalog(catalog: Enumerated | undefined) {
			self.elements.clear();
			if (!catalog?.id) {
				return superSetField("buildingPartCatalog", undefined);
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
		get isReadyForGeocode(): boolean {
			if (!!self.geoAddress) {
				return true;
			} else if (!!self.street && !!self.zip && !!self.city && !!self.country) {
				return true;
			}
			return false;
		},
		get geoInput(): string {
			if (!!self.geoAddress) {
				return self.geoAddress;
			} else if (!!self.street && !!self.zip && !!self.city && !!self.country) {
				return self.street + ", " + self.zip + " " + self.city + ", " + self.country;
			}
			return "";
		},
	}))
	.actions((self) => ({
		resolveGeocode() {
			return flow(function* () {
				if (self.isReadyForGeocode) {
					transaction(() => {
						self.geoCoordinates = undefined;
						self.geoZoom = undefined;
					});
					try {
						const geocodeResponse: AxiosResponse<GeocodeResponse> = yield API.post(
							Config.getRestUrl("building", "buildings/location"),
							{
								geoAddress: !!self.geoAddress ? self.geoAddress : undefined,
								street: self.street,
								zip: self.zip,
								city: self.city,
								country: self.country?.name
							} as GeocodeRequest
						);
						if (geocodeResponse.status === 200) {
							const geoCoordinates = geocodeResponse.data.geoCoordinates;
							const geoZoom = geocodeResponse.data.geoZoom;
							transaction(() => {
								self.geoCoordinates = geoCoordinates;
								self.geoZoom = geoZoom;
							});
						} else {
							self.geoCoordinates = "Kann nicht auflösen";
						}
					} catch (error: any) {
						Logger.error("Geocode request failed", error);
					}
				}
			})();
		}
	}))
	.actions(self => {
		let timeout: any;
		return {
			afterCreate() {
				addDisposer(self, reaction(
					() => {
						return { input: self.geoInput, inTrx: self.rootStore.isInTrx };
					},
					() => {
						if (timeout) {
							clearTimeout(timeout);
							timeout = null;
						}
						if (self.rootStore.isInTrx && self.isReadyForGeocode) {
							timeout = setTimeout(() => {
								self.resolveGeocode();
							}, 500);
						}
					}
				))
			}
		}
	})
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
