
package fm.comunas.fm.lead.service.api;

import fm.comunas.fm.account.model.enums.CodeAccountType;
import fm.comunas.fm.contact.model.enums.CodeSalutation;
import fm.comunas.fm.lead.model.DocLead;
import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.oe.model.ObjUser;

import java.util.List;

public interface LeadConversionService {

	//@formatter:off
	List<Aggregate> convertLead(
		DocLead lead,
		ObjUser owner,
		Boolean doCreateAccount,
		Integer accountId,
		CodeAccountType accountType,
		String accountName,
		Boolean doCreateContact,
		Integer contactId,
		CodeSalutation contactSalutation,
		String contactFirstName,
		String contactLastName,
		Boolean doCreateDoc,
		String docType,
		String docName
	);
	//@formatter:on

}
