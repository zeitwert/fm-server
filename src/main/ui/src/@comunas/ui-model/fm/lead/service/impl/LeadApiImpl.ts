import { EntityTypeRepository } from "../../../../app/common";
import { Config } from "../../../../app/common/config/Config";
import { API, API_HEADERS } from "../../../../app/common/service/Api";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { ConversionInfo, LeadModel, LeadSnapshot } from "../../model/LeadModel";
import { LeadApi } from "../LeadApi";

const MODULE = "lead";
const PATH = "leads";
const TYPE = "lead";
const INCLUDES =
	"include[lead]=refObj,refDoc,documents,assignee,account,contact&include[account]=mainContact,contacts&include[contact]=account";

export class LeadApiImpl extends AggregateApiImpl<LeadSnapshot> implements LeadApi {
	constructor() {
		const PROPS = Object.keys(LeadModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["isInWork"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			refObj: "obj",
			refDoc: "doc",
			documents: "document",
			assignee: "obj",
			account: "account",
			contact: "contact"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async convertLead(conversionInfo: ConversionInfo): Promise<EntityTypeRepository> {
		const url =
			Config.getApiUrl(MODULE, "conversion") + "?include[leadConversion]=account,contact,opportunity,advice";
		const { account, contact, docInfo } = conversionInfo;
		const attributes = {
			leadId: conversionInfo.leadId,
			ownerId: conversionInfo.owner.id,
			doCreateAccount: account.doCreate,
			accountId: account.info?.id,
			accountName: account.caption,
			doCreateContact: contact.doCreate,
			contactId: contact.info?.id,
			contactSalutation: contact.salutation?.id,
			contactFirstName: contact.firstName,
			contactLastName: contact.lastName,
			doCreateDoc: docInfo.doCreate,
			docType: docInfo.docType?.id,
			docName: docInfo.caption
		};
		const body = {
			data: {
				type: "leadConversion",
				attributes: attributes
			}
		};
		const response = await API.post(url, body, { headers: API_HEADERS });
		return await this.deserializeData(response.data);
	}
}
