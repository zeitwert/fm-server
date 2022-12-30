import Logger from "loglevel";
import { transaction } from "mobx";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { DOC_TRANSITION_API } from "../service/DocPartTransitionApi";
import { CaseStage } from "./BpmModel";
import { Doc } from "./DocModel";
import { DocPartTransition, DocPartTransitionModel } from "./DocPartTransitionModel";

const MstDocPartTransitionStoreModel = types
	.model("DocTransitionStore")
	.props({
		transitions: types.optional(types.array(DocPartTransitionModel), [])
	})
	.views((self) => ({
		get stageTransitions(): DocPartTransition[] {
			return self.transitions
				.filter((transition) => transition.oldCaseStage !== undefined && transition.newCaseStage !== undefined)
				.sort((t1, t2) => (t1.modifiedAt! < t2.modifiedAt! ? 1 : -1));
		}
	}))
	.actions((self) => ({
		updateTransitions(repository: any) {
			transaction(() => {
				self.transitions.clear();
				if (repository.docTransition) {
					for (const docTransition of Object.values(repository.docTransition)) {
						self.transitions.push(DocPartTransitionModel.create(docTransition as any));
					}
				}
			});
		}
	}))
	.actions((self) => ({
		loadTransitions(doc: Doc) {
			return flow(function* () {
				try {
					const repository = yield DOC_TRANSITION_API.find(doc);
					self.updateTransitions(repository);
				} catch (error: any) {
					Logger.error("Failed to load doc transitions", error);
				}
			})();
		},
		findLatestTransitionFrom(stage: CaseStage): DocPartTransition | undefined {
			return self.stageTransitions.find((transition) => transition.oldCaseStage!.id === stage.id);
		},
		findLatestTransitionTo(stage: CaseStage): DocPartTransition | undefined {
			return self.stageTransitions.find((transition) => transition.newCaseStage!.id === stage.id);
		}
	}));

type MstDocPartTransitionStoreType = typeof MstDocPartTransitionStoreModel;
interface MstDocPartTransitionStore extends MstDocPartTransitionStoreType { }

export const DocPartTransitionStoreModel: MstDocPartTransitionStore = MstDocPartTransitionStoreModel;
export type DocPartTransitionStoreModelType = typeof DocPartTransitionStoreModel;
export interface DocPartTransitionStore extends Instance<DocPartTransitionStoreModelType> { }
export type DocPartTransitionStoreSnapshot = SnapshotIn<DocPartTransitionStoreModelType>;
export type DocPartTransitionPayload = Omit<DocPartTransitionStoreSnapshot, "id">;
