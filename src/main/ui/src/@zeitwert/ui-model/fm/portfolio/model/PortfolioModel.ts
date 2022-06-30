
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated, EnumeratedModel } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { PortfolioStore } from "./PortfolioStore";

const MstPortfolioModel = ObjModel.named("Portfolio")
	.props({
		account: types.maybe(types.reference(AccountModel)),
		//
		name: types.maybe(types.string),
		portfolioNr: types.maybe(types.string),
		description: types.maybe(types.string),
		includes: types.optional(types.array(EnumeratedModel), []),
		excludes: types.optional(types.array(EnumeratedModel), []),
		buildings: types.optional(types.array(EnumeratedModel), [])
	})
	.views((self) => ({
		get allowStore(): boolean {
			return !!self.name && !!self.account;
		},
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setAccount(id: string) {
			id && (await (self.rootStore as PortfolioStore).accountsStore.loadAccount(id));
			return superSetField("account", id);
		}
		function addIncludeObj(obj: Enumerated) {
			self.includes.push(obj);
			self.calcOnServer();
		}
		function removeIncludeObj(id: string) {
			const index = self.includes.findIndex((o) => o.id === id);
			self.includes.splice(index, 1);
			self.calcOnServer();
		}
		function addExcludeObj(obj: Enumerated) {
			self.excludes.push(obj);
			self.calcOnServer();
		}
		function removeExcludeObj(id: string) {
			const index = self.excludes.findIndex((o) => o.id === id);
			self.excludes.splice(index, 1);
			self.calcOnServer();
		}
		async function setField(field: string, value: any) {
			switch (field) {
				case "account": {
					return setAccount(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setAccount,
			addIncludeObj,
			removeIncludeObj,
			addExcludeObj,
			removeExcludeObj,
			setField
		};
	});

type MstPortfolioType = typeof MstPortfolioModel;
export interface MstPortfolio extends MstPortfolioType { }
export const PortfolioModel: MstPortfolio = MstPortfolioModel;
export interface Portfolio extends Instance<typeof PortfolioModel> { }
export type PortfolioSnapshot = SnapshotIn<typeof MstPortfolioModel>;
export type PortfolioPayload = Omit<PortfolioSnapshot, "id">;
