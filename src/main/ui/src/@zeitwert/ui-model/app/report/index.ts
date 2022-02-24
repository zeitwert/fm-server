import { ReportEngineImpl } from "./service/impl/ReportEngineImpl";
import { ReportServiceImpl } from "./service/impl/ReportServiceImpl";

export * from "./model/Datamart";
export * from "./model/Layout";
export * from "./model/Provider";
export * from "./model/Template";
export * from "./service/KanbanApi";
export * from "./service/ReportEngine";
export * from "./service/ReportService";

export const reportEngine = new ReportEngineImpl();
export const reportService = new ReportServiceImpl();
