
package io.zeitwert.ddd.oe.adapter.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

@RestController("userDocumentController")
@RequestMapping("/rest/oe/users")
public class UserDocumentController {

	@Autowired
	private ObjUserRepository repo;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
		Integer documentId = this.repo.get(id).getAvatarImageId();
		return this.documentController.getContent(documentId);
	}

	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.POST)
	public ResponseEntity<Void> setAvatar(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
		try {
			ObjUser user = this.repo.get(id);
			ObjDocument document = user.getAvatarImage();
			CodeContentType contentType = CodeContentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			document.storeContent(contentType, file.getBytes());
			user.calcAll();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
