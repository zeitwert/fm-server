
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { NoteModel, NoteSnapshot } from "../../model/NoteModel";
import { NoteApi } from "../NoteApi";

const MODULE = "collaboration";
const PATH = "notes";
const TYPE = "note";
const INCLUDES = "";

export class NoteApiImpl extends AggregateApiImpl<NoteSnapshot> implements NoteApi {
	constructor() {
		const PROPS = Object.keys(NoteModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["contacts", "documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			//documents: "document",
			//mainContact: "contact",
			//holdings: "holding"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
