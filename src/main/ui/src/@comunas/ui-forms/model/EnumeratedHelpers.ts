
import { Config, Enumerated, EnumeratedModel } from "@comunas/ui-model";
import { types } from "mobx-state-tree";
import { AccessorDependentQuery, Query, Source } from "mstform";

const API_BASE_URL = Config.getApiUrl("##", "##").replace("/##/##", "");
const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

export function replaceUrlPlaceholders(url: string): string {
	return url.replace("{{enumBaseUrl}}", ENUM_BASE_URL).replace("{{apiBaseUrl}}", API_BASE_URL);
}

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
				const response = await window.fetch(replaceUrlPlaceholders(sourceOrUrl));
				return response.json();
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

// redefined because mstform only has model | null (instead of undefined)
// function maybeModel<M extends IAnyModelType>(_model: M) {
// 	return new Converter<Instance<M> | undefined, Instance<M> | undefined>({
// 		emptyRaw: undefined,
// 		emptyValue: undefined,
// 		defaultControlled: controlled.value,
// 		convert(raw) {
// 			return raw;
// 		},
// 		render(value) {
// 			return value;
// 		},
// 	});
// }
