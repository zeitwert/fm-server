import { createEntityApi } from "@/common/api/entityApi";
import type { User, UserListItem } from "./types";

export const userApi = createEntityApi<User>({
	module: "oe",
	path: "users",
	type: "user",
	includes: "include[user]=avatar",
	relations: {
		avatar: "document",
	},
});

export const userListApi = createEntityApi<UserListItem>({
	module: "oe",
	path: "users",
	type: "user",
	includes: "",
	relations: {},
});
