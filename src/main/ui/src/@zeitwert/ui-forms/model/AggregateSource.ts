
import { asEnumerated, Enumerated, EnumeratedModel } from "@zeitwert/ui-model";
import { AggregateApi } from "@zeitwert/ui-model/ddd/aggregate/service/AggregateApi";
import { SnapshotIn, types } from "mobx-state-tree";
import { Query, Source } from "mstform";

const AggregateContainer = types.model(
	"AggregateContainer",
	{
		id: types.identifier,
		entryMap: types.map(EnumeratedModel),
	}
)
	.actions((self) => ({
		addItem(item: Enumerated) {
			self.entryMap.set(item.id, item);
		},
		removeItem(item: Enumerated) {
			self.entryMap.delete(item.id);
		},
	}));

let id = 0;
function getNextId() {
	return "aggregate-" + id++;
}

export type AggregateSource = AggregateApi<any> | ((q?: Query) => Promise<Enumerated[]>);

export function aggregateSource(sourceOrApi: AggregateSource) {
	const container = AggregateContainer.create({ id: getNextId() });
	if (typeof sourceOrApi === "object") {
		return new Source({
			entryMap: container.entryMap,
			load: async (q: Query): Promise<SnapshotIn<Enumerated>[]> => {
				try {
					const entityTypeRepo = await sourceOrApi.getAggregates(q?.searchText ? `filter[searchText]=${q.searchText}` : undefined);
					const entityRepo = entityTypeRepo[sourceOrApi.getItemType()] ?? [];
					const items = Object.keys(entityRepo).map((key) => asEnumerated(entityRepo[key])!);
					return items;
				} catch (e) {
					console.error("AggregateSource.load() failed", e);
					throw e;
				}
				return [];
			},
		});
	} else {
		return new Source({
			entryMap: container.entryMap,
			load: sourceOrApi,
		});
	}
}
