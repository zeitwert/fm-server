import Logger from "loglevel";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Aggregate } from "../../../ddd/aggregate/model/AggregateModel";
import { Task } from "../../../fm/task/model/TaskModel";
import { TASK_API } from "../../../fm/task/service/TaskApi";
import { API, Config } from "../../common";
import { UserInfo } from "./SessionInfo";

const MAX_NEXT_TASKS = 3;

export const SeriesDataModel = types.model("SeriesData", {
	name: types.string,
	y: types.number
});

export const MessageModel = types.model("Message", {
	status: types.string,
	text: types.string
});

export interface BreadCrumb {
	title: string;
	iconCategory?: string;
	iconName?: string;
}

const MstAppStoreModel = types
	.model("AppStore", {
		metadataFormDef: types.maybe(types.frozen<any>()),
		//
		users: types.maybe(types.frozen<UserInfo[]>()),
		breadCrumbs: types.optional(types.array(types.frozen<BreadCrumb>()), []),
		//
		// recentItems: types.optional(types.array(types.reference(ItemModel)), []),
		// frequentItems: types.optional(types.array(types.reference(ItemModel)), []),
		//
		msg: types.maybe(MessageModel),
		leads: types.array(types.frozen()),
		leadsAssignee: types.maybe(types.string),
		lifeEvents: types.array(types.frozen()),
		lifeEventsAssignee: types.maybe(types.string),
		tasks: types.optional(types.array(types.frozen<Task>()), []),
		events: types.optional(types.frozen(), [])
	})
	.views((self) => ({
		getUser(id: string) {
			if (self.users) {
				return self.users.find((user: UserInfo): any => user.id === id);
			}
			return undefined;
		}
	}))
	.actions((self) => ({
		addBreadCrumbs(crumbs: BreadCrumb[]) {
			self.breadCrumbs.push(...crumbs);
		},
		removeBreadCrumbs(number: number) {
			self.breadCrumbs.splice(-number, number);
		},
	}))
	.actions((self) => ({
		getMetadataFormDefinition() {
			if (self.metadataFormDef) {
				return Promise.resolve(self.metadataFormDef);
			}
			return flow(function* () {
				try {
					const response = yield API.get(Config.getEnumUrl("base", "codeMetadataFormDef"));
					self.metadataFormDef = response.data;
					return self.metadataFormDef;
				} catch (error: any) {
					Logger.error("Failed to get metadata form definition");
					return Promise.reject(error);
				}
			})();
		}
	}))
	.actions((self) => ({
		// loadRecentItems(user: UserInfo) {
		// 	return flow(function* () {
		// 		try {
		// 			const repository = yield ITEM_API.getRecentByUser(user);
		// 			transaction(() => {
		// 				//self.updateStaticData(repository);
		// 				if (repository.item) {
		// 					Object.keys(repository.item)
		// 						.map((id) => repository.item[id])
		// 						.forEach((item) => self.recentItems.push(item.id));
		// 				}
		// 			});
		// 		} catch (error: any) {
		// 			Logger.error("Failed to load recent items");
		// 		}
		// 	})();
		// },
		// loadFrequentItems(user: UserInfo) {
		// 	return flow(function* () {
		// 		try {
		// 			const repository = yield ITEM_API.getFrequentByUser(user);
		// 			transaction(() => {
		// 				//self.updateStaticData(repository);
		// 				if (repository.item) {
		// 					Object.keys(repository.item)
		// 						.map((id) => repository.item[id])
		// 						.forEach((item) => self.frequentItems.push(item.id));
		// 				}
		// 			});
		// 		} catch (error: any) {
		// 			Logger.error("Failed to load frequent items");
		// 		}
		// 	})();
		// },
		getUsers() {
			if (self.users) {
				return Promise.resolve(self.users);
			}
			return flow<UserInfo[], any[]>(function* (): any {
				try {
					const response = yield API.get(Config.getEnumUrl("oe", "objUser"));
					self.users = response.data;
					return self.users;
				} catch (error: any) {
					Logger.error("Failed to get users");
					return Promise.reject(error);
				}
			})();
		}
	}))
	.actions((self) => ({
		setItem(item: Aggregate) {
			// add item to recent or frequent items, put to item list to make sure it is available
			// const data = ItemModel.create(item.apiSnapshot);
			// self.updateStaticData({
			// 	item: {
			// 		[item.id]: data
			// 	}
			// });
			// if (self.recentItems.findIndex((ri) => ri.id === item.id) === -1) {
			// 	self.recentItems.push(data);
			// } else if (self.frequentItems.findIndex((fi) => fi.id === item.id) === -1) {
			// 	self.frequentItems.push(data);
			// }
		}
	}))
	.actions((self) => ({
		hideMsg() {
			self.msg = undefined;
		},
		showMsg(status: string, text: string) {
			self.msg = {
				status: status,
				text: text
			};
		},
		getLeads(assignee: string | undefined = self.leadsAssignee) {
			self.leadsAssignee = assignee;
			return flow(function* () {
				try {
					const leadData = yield API.get(Config.getMockUrl("t1", "lead", "leads")).then(
						(response: any) => response.data.leads
					);
					self.leads = leadData /*.filter((l: any) => l.assignee === assignee)*/;
				} catch (error: any) {
					Logger.error("Failed to get leads", error);
				}
			})();
		},
		getLifeEvents(assignee: string | undefined = self.lifeEventsAssignee) {
			self.lifeEventsAssignee = assignee;
			return flow(function* () {
				try {
					const lifeEventData = yield API.get(Config.getMockUrl("t1", "lifeEvent", "lifeEvents")).then(
						(response: any) => response.data.lifeEvents
					);
					self.lifeEvents = lifeEventData /*.filter((l: any) => l.assignee === assignee)*/;
				} catch (error: any) {
					Logger.error("Failed to get life events", error);
				}
			})();
		},
		getTasks() {
			return flow(function* () {
				try {
					self.tasks = yield TASK_API.findUpcomingTasks(MAX_NEXT_TASKS);
				} catch (error: any) {
					Logger.error("Failed to get tasks", error);
				}
			})();
		}
	}))
	.actions((self) => ({
		removeLead(leadId: string) {
			self.leads.remove(self.leads.find((l) => l.id === leadId));
		},
		removeLifeEvent(lifeEventId: string) {
			self.lifeEvents.remove(self.lifeEvents.find((l) => l.id === lifeEventId));
		}
	}))
	.actions((self) => ({
		convertLead(leadId: string, init: string) {
			console.log("Convert lead: ", init);

			self.removeLead(leadId);
			/*
			var gearUrl = "/api/case-definitions?nameFilter=Opportunity&size=1&view=full";
			let url = "/api/cases";

			let lead = self.leads.find((l: any) => {
				return l.id === leadId;
			});

			if (lead) {
				// get gear id
				flow(function* () {
					try {
						var caseDefinitions = yield Proxy.get("e1", gearUrl)
							.then((response) => {
								return response.data.data;
							})
							.then((result: any[]) => {
								return result;
							});

						if (caseDefinitions && caseDefinitions.length > 0) {
							let gearId = caseDefinitions[0].id;
							let data = {
								"definitionId": gearId,
								"_hideHeaderPane": true,
								"_hideMenuActionsPane": true,
								"_hideSavePane": true,
								"ed_area": lead.opportunityArea,
								"ed_init": init,
								"ed_lead": lead,
								"name": lead.descriptionShort + " (" + lead.account.advisee.name + ")",
								"ed_account": {
									"id": lead.account.id,
									"name": lead.account.advisee.name,
									"advisee": lead.account.advisee,
									"customerType": lead.account.customerType,
									"overview": lead.account.overview,
									// TODO: belongs to account or advisee?
									"maritalStatus": lead.account.maritalStatus
								}
							};

							flow(function* () {
								try {
									yield Proxy.post("e1", url, data)
										.then((response) => {
											self.removeLead(leadId);
											self.showMsg("success", "Opportunity successfully created");
											return response;
										})
										.catch((event: any) => {
											self.showMsg("error", "Failed to convert lead");
											Logger.error("Failed to convert lead, !!event ? event : "");
										});
								} catch (error: any) {
									Logger.error("Failed to convert lead", error);
								}
							})();
						}
					} catch (error: any) {
						Logger.error("Failed to convertLead", error);
					}
				})();
			}
			*/
		}
	}));

type MstAppStoreType = typeof MstAppStoreModel;
interface MstAppStore extends MstAppStoreType { }

export const AppStoreModel: MstAppStore = MstAppStoreModel;
export type AppStoreModelType = typeof AppStoreModel;
export interface AppStore extends Instance<AppStoreModelType> { }
export type AppStoreSnapshot = SnapshotIn<AppStoreModelType>;
export type AppStorePayload = Omit<AppStoreSnapshot, "id">;
