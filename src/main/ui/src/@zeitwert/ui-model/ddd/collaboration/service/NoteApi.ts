
import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { NoteSnapshot } from "../model/NoteModel";
import { NoteApiImpl } from "./impl/NoteApiImpl";

export interface NoteApi extends AggregateApi<NoteSnapshot> { }

export const NOTE_API: NoteApi = new NoteApiImpl();
