
import { API, Config, Enumerated } from "@zeitwert/ui-model";
import { SnapshotIn, types } from "mobx-state-tree";
import { Query, Source } from "mstform";

const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

const EnumeratedContainer = types.model(
	"EnumeratedContainer",
	{
		entryMap: types.map(types.frozen<Enumerated>()),
	}
);

export type EnumSource = string | ((q?: Query) => Promise<Enumerated[]>);

export function enumeratedSource(sourceOrUrl: EnumSource) {
	const container = EnumeratedContainer.create({ entryMap: {} });
	if (typeof sourceOrUrl === "string") {
		return new Source({
			entryMap: container.entryMap,
			load: async (q: Query): Promise<SnapshotIn<Enumerated>[]> => {
				const response = await API.get(ENUM_BASE_URL + "/" + sourceOrUrl);
				return response.data;
			},
		});
	} else {
		return new Source({
			entryMap: container.entryMap,
			load: sourceOrUrl,
		});
	}
}
