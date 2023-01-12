
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
		ratingId: types.maybe(types.string),
		ratingSeqNr: types.maybe(types.number),
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
		async function setPartCatalog(catalog: Enumerated | undefined) {
			superSetField("partCatalog", catalog);
			self.elements.clear();
			if (!!catalog) {
				await self.calcOnServer();
			}
		}
		async function setField(field: string, value: any) {
			switch (field) {
				case "account": {
					return setAccount(value);
				}
				case "partCatalog": {
					return setPartCatalog(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setAccount,
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
	}))
	.views((self) => ({
		get hasCoverFoto(): boolean {
			return !!self.coverFoto?.id && !!self.coverFoto?.contentType?.id;
		},
		get coverFotoUrl(): string | undefined {
			if (self.coverFoto?.id && self.coverFoto?.contentType?.id) {
				return Config.getRestUrl("dms", "documents/" + self.coverFoto?.id + "/content");
			}
			return "/missing.jpg";
		},
		get locationUrl(): string | undefined {
			if (self.geoCoordinates) {
				return Config.getRestUrl("building", "buildings/" + self.id + "/location");
			}
			return "/missing.jpg";
		},
		get weightSum() {
			return self.elements.reduce((sum, element) => { return sum + (element.weight || 0.0); }, 0.0);
		}
	}))
	.views((self) => ({
		get isReadyForGeocode(): boolean {
			if (!!self.geoAddress) {
				return true;
			} else if (!!self.zip && !!self.city && !!self.country) {
				return true;
			}
			return false;
		},
		get geoInput(): string {
			if (!!self.geoAddress) {
				return self.geoAddress;
			} else if (!!self.zip && !!self.city && !!self.country) {
				return (self.street ? self.street + ", " : "") + self.zip + " " + self.city + ", " + self.country;
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
	.actions((self) => ({
		async addRating() {
			await self.execOperation(["addRating", "calculationOnly"]);
			self.rootStore.startTrx();
		},
		moveRatingStatus(ratingStatusId: string) {
			self.rootStore.startTrx();
			self.setField("ratingStatus", { id: ratingStatusId, name: "" });
			return self.rootStore.store();
		}
	}))
	.actions(self => {
		let geoTimeout: any;
		return {
			afterCreate() {
				addDisposer(self, reaction(
					() => {
						return { input: self.geoInput, inTrx: self.rootStore.isInTrx };
					},
					() => {
						if (geoTimeout) {
							clearTimeout(geoTimeout);
							geoTimeout = null;
						}
						if (self.rootStore.isInTrx && self.isReadyForGeocode) {
							geoTimeout = setTimeout(() => {
								self.resolveGeocode();
							}, 500);
						}
					}
				));
				addDisposer(self, reaction(
					() => {
						return { input: self.ratingDate, inTrx: self.rootStore.isInTrx };
					},
					() => {
						if (self.rootStore.isInTrx) {
							const year = self.ratingDate?.getFullYear();
							self.elements.forEach(e => {
								e.setField("conditionYear", year);
							});
						}
					}
				));
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
interface MstBuilding extends MstBuildingType { }

export const BuildingModel: MstBuilding = MstBuildingModel;
export type BuildingModelType = typeof BuildingModel;
export interface Building extends Instance<BuildingModelType> { }
export type BuildingSnapshot = SnapshotIn<BuildingModelType>;
export type BuildingPayload = Omit<BuildingSnapshot, "id">;
