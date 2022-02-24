
package io.zeitwert.fm.lead.service.api;

import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.oe.model.ObjUser;

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
