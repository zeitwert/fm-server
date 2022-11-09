
package io.zeitwert.fm.account.adapter.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

@RestController("accountDocumentController")
@RequestMapping("/rest/account/accounts")
public class AccountDocumentController {

	@Autowired
	private ObjAccountRepository repo;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/{img}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id, @PathVariable String img) {
		ObjAccount account = this.repo.get(id);
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

	@RequestMapping(value = "/{id}/{img}", method = RequestMethod.POST)
	public ResponseEntity<Void> setImage(@PathVariable Integer id, @PathVariable String img,
			@RequestParam("file") MultipartFile file) {
		try {
			ObjAccount account = this.repo.get(id);
			ObjDocument document = null;
			if ("logo".equals(img)) {
				document = account.getLogoImage();
			} else if ("banner".equals(img)) {
				document = account.getBannerImage();
			} else {
				return ResponseEntity.badRequest().body(null);
			}
			CodeContentType contentType = CodeContentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			document.storeContent(contentType, file.getBytes());
			account.calcAll();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
