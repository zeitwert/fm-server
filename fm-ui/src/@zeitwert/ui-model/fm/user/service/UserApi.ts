
import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { UserSnapshot } from "../model/UserModel";
import { UserApiImpl } from "./impl/UserApiImpl";

export interface UserApi extends AggregateApi<UserSnapshot> { }

export const USER_API: UserApi = new UserApiImpl();
