
import { API, Config } from "../../../common";
import { KanbanApi } from "../KanbanApi";

const API_HEADERS = {
	"Content-Type": "application/vnd.api+json",
	"Accept": "application/json,application/vnd.api+json"
};

const baseUrl = Config.getEnvParam("API_BASE_URL");
const enumBaseUrl = "{{enumBaseUrl}}/";
const apiBaseUrl = "{{apiBaseUrl}}/";

export class KanbanApiImpl implements KanbanApi {
	// TODO: Cleanup
	private getUrl(path: string) {
		if (path.startsWith(enumBaseUrl)) {
			return baseUrl.replace("{type}", "enum").replace("{api}", path.substr(enumBaseUrl.length));
		} else if (path.startsWith(apiBaseUrl)) {
			return baseUrl.replace("{type}", "api").replace("{api}", path.substr(apiBaseUrl.length));
		}
		return path;
	}

	async getHeaders(path: string): Promise<any> {
		const url = this.getUrl(path);
		const response = await API.get(url, { headers: API_HEADERS });
		return response.data;
	}

	async updateItem(itemPath: string, itemType: string, itemId: string, field: string, id: string): Promise<any> {
		const url = this.getUrl(itemPath + "/" + itemId);
		const body = JSON.stringify({
			data: {
				type: itemType,
				id: itemId,
				attributes: {
					[field]: {
						id: id
					}
				}
			}
		});
		const response = await API.patch(url, body, { headers: API_HEADERS });
		return response.data;
	}
}
