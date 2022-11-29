
package io.zeitwert.fm.account.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
		if ("logo".equals(img)) {
			Integer documentId = account.getLogoImageId();
			return this.documentController.getContent(documentId);
		} else if ("banner".equals(img)) {
			String headerImage = HeaderImage
					.replace("{logo}", "http://localhost:8080/rest/account/accounts/" + account.getId() + "/logo")
					.replace("{account}", account.getCaption()).replace("{tenant}", account.getTenant().getCaption());
			ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename("banner.svg").build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(contentDisposition);
			return ResponseEntity.ok().contentType(new MediaType("image", "svg+xml")).headers(headers)
					.body(headerImage.getBytes());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	private String HeaderImage = """
			<?xml version="1.0" encoding="UTF-8"?>
			<svg width="300px" height="50px" viewBox="0 0 300 50" xmlns="http://www.w3.org/2000/svg" version="1.1">
				<style>
					.account {
						font: bold 24px arial;
						fill: #333;
					}
					.tenant {
						font: 12px arial;
						fill: #444;
					}
				</style>
				<image x="0" y="0" height="50" width="50" href="{logo}"/>
				<text x="50" y="25" class="account">{account}</text>
				<text x="51" y="40" class="tenant">{tenant}</text>
			</svg>
						""";

}
