
import { API, Config, Enumerated, EnumeratedModel } from "@zeitwert/ui-model";
import { types } from "mobx-state-tree";
import { AccessorDependentQuery, Query, Source } from "mstform";

const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

export const EnumeratedContainer = types.model(
	"EnumeratedContainer",
	{
		entryMap: types.map(EnumeratedModel),
	}
);

export type EnumSource = string | ((q: Query) => Promise<Enumerated[]>);

export function enumeratedSource(sourceOrUrl: EnumSource, dependentQuery?: AccessorDependentQuery<any>) {
	const container = EnumeratedContainer.create({ entryMap: {} });
	let source: Source<typeof EnumeratedModel, any>;
	if (typeof sourceOrUrl === "string") {
		source = new Source({
			entryMap: container.entryMap,
			load: async () => {
				const response = await API.get(ENUM_BASE_URL + "/" + sourceOrUrl);
				return response.data;
			},
		});
	} else {
		source = new Source({
			entryMap: container.entryMap,
			load: sourceOrUrl,
		});
	}
	return {
		references: {
			source: source,
			dependentQuery: dependentQuery
		}
	}
}
