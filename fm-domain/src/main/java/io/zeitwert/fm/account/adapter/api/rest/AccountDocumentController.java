package io.zeitwert.fm.account.adapter.api.rest;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("accountDocumentController")
@RequestMapping("/rest/account/accounts")
public class AccountDocumentController {

	@Autowired
	SessionContext sessionContext;
	@Autowired
	private ObjAccountRepository accountRepository;
	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/logo", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		ObjAccount account = this.accountRepository.get(id);
		Integer documentId = (Integer) account.getLogoImageId();
		if (documentId == null) {
			return ResponseEntity.noContent().build();
		}
		return this.documentController.getContent(documentId);
	}

}
