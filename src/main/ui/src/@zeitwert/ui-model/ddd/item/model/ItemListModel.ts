import { session } from "@zeitwert/ui-model/app";
import Logger from "loglevel";
import { transaction } from "mobx";
import { cast, flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Config } from "../../../app/common";
import { KANBAN_API, reportEngine, reportService, Template } from "../../../app/report";

const API_BASE_URL = Config.getApiUrl("##", "##").replace("/##/##", "");
const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

const MstItemListModel = types
	.model("ItemList", {
		datamart: types.string,
		templateMap: types.optional(types.map(types.frozen<Template>()), {}),
		template: types.maybe(types.frozen<Template>()),
		reportData: types.maybe(types.frozen()),
		modifiedAt: types.maybe(types.Date)
	})
	.views((self) => ({
		get templateList(): Template[] {
			return Array.from(self.templateMap.values());
		},
		get itemCount(): number {
			return self.reportData?.data?.length || 0;
		}
	}))
	.actions((self) => ({
		init() {
			transaction(() => {
				self.reportData = undefined;
				self.modifiedAt = undefined;
			});
		},
		replaceUrls() {
			const layout = self.template!.layout;
			if (layout.layoutType === "kanban" && !!layout.layout?.header?.dynamic?.url) {
				layout.layout.header.dynamic.url = layout.layout.header.dynamic.url.replace(
					"{{enumBaseUrl}}",
					ENUM_BASE_URL
				);
				layout.layout.header.dynamic.url = layout.layout.header.dynamic.url.replace(
					"{{apiBaseUrl}}",
					API_BASE_URL
				);
			}
		},
		async getKanbanHeaders() {
			const layout = self.template!.layout;
			if (layout.layoutType === "kanban" && !!layout.layout?.header?.dynamic?.url) {
				const header = layout.layout.header;
				let getResult: any = [];
				layout.layout!.header!.static = [];
				try {
					await KANBAN_API.getHeaders(header.dynamic.url).then((data: any) => (getResult = data));
					getResult.forEach((item: any) => {
						layout.layout!.header!.static.push({
							value: item[header.dynamic.valueField],
							displayName: item[header.dynamic.displayNameField]
						});
					});
				} catch (err) {
					console.error("Failed to fetch kanban dynamic", err);
				}
			}
		}
	}))
	.actions((self) => {
		return {
			clear() {
				transaction(() => {
					self.templateMap = cast({});
					self.init();
				});
			},
			callReportEngine(method: string, templateId?: string, params?: any) {
				self.init();
				if (!templateId && !self.template) {
					return null as any;
				}
				return flow(function* () {
					try {
						session.startNetwork();
						self.template = templateId ? yield reportService.template(templateId) : self.template;
						if (!!self.template) {
							self.replaceUrls();
							yield self.getKanbanHeaders();
							self.reportData = yield reportEngine[method](
								self.template.datamart,
								self.template.layout,
								Object.assign({}, self.template.params, params),
								self.template.sort,
								self.template.limit
							);
							self.modifiedAt = self.reportData.meta?.latestModifiedAt
								? new Date(self.reportData.meta?.latestModifiedAt)
								: undefined;
						}
						return self.reportData;
					} catch (error: any) {
						Logger.error(`Failed to execute template ${self.template}`, error);
						return Promise.reject(error);
					} finally {
						session.stopNetwork();
					}
				})();
			}
		};
	})
	.actions((self) => ({
		initTemplates() {
			return flow<void, any[]>(function* (): any {
				try {
					const templates: Template[] = yield reportService.templates(self.datamart);
					transaction(() => {
						templates.forEach((template) => self.templateMap.set(template.id, template));
					});
				} catch (error: any) {
					Logger.error("Failed to init templates", error);
					return Promise.reject(error);
				}
			})();
		},
		executeTemplate(templateId?: string, params?: any): Promise<any> {
			return flow<any, any[]>(function* () {
				return yield self.callReportEngine("execute", templateId, params);
			})();
		}
	}));

type MstItemListType = typeof MstItemListModel;
export interface MstItemList extends MstItemListType { }
export const ItemListModel: MstItemList = MstItemListModel;
export interface ItemList extends Instance<typeof ItemListModel> { }
export type MstItemListSnapshot = SnapshotIn<typeof MstItemListModel>;
export interface ItemListSnapshot extends MstItemListSnapshot { }
