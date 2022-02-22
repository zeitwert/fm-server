import moment from "moment";
import { API, Config, EntityTypeRepository } from "../../../../app/common";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { ContactModel, ContactSnapshot } from "../../model/ContactModel";
import { ContactApi } from "../ContactApi";

const MODULE = "contact";
const PATH = "contacts";
const TYPE = "contact";
const INCLUDES = "include[contact]=account&include[account]=mainContact,contacts";

export class ContactApiImpl extends AggregateApiImpl<ContactSnapshot> implements ContactApi {
	constructor() {
		const PROPS = Object.keys(ContactModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["refObj", "documents", "addresses", "lifeEvents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			// refObj: "obj",
			// documents: "document",
			account: "account"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async getByEmail(email: string): Promise<EntityTypeRepository> {
		const response = await API.get(
			Config.getApiUrl(MODULE, PATH) + "?filter[email]=" + email + "&include[contact]=account"
		);
		return this.deserializeData(response.data);
	}

	async getAllLifeEvents(): Promise<EntityTypeRepository> {
		const from = moment(Date.now()).format("YYYY-MM-DD HH:mm:ss");
		const to = moment(Date.now()).add(7, "days").format("YYYY-MM-DD HH:mm:ss");
		const response = await API.get(Config.getApiUrl(MODULE, "lifeEvents?from=" + from + "&to=" + to));
		return response.data;
	}

}
