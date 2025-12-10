import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { AccountSnapshot } from "../model/AccountModel";
import { AccountApiImpl } from "./impl/AccountApiImpl";

export interface AccountApi extends AggregateApi<AccountSnapshot> { }

export const ACCOUNT_API: AccountApi = new AccountApiImpl();
