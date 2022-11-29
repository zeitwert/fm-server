
package io.zeitwert.fm.account.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;

@RestController("accountDocumentController")
@RequestMapping("/rest/account/accounts")
public class AccountDocumentController {

	@Autowired
	private ObjAccountRepository accountCache;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/{img}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id, @PathVariable String img) {
		ObjAccount account = this.accountCache.get(id);
		Integer documentId = null;
		if ("logo".equals(img)) {
			documentId = account.getLogoImageId();
		} else if ("banner".equals(img)) {
			documentId = account.getBannerImageId();
		} else {
			return ResponseEntity.notFound().build();
		}
		return this.documentController.getContent(documentId);
	}

}
