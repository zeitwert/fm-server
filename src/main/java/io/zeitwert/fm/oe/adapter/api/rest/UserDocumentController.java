
package io.zeitwert.fm.oe.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.oe.model.ObjUserFM;

@RestController("userDocumentController")
@RequestMapping("/rest/oe/users")
public class UserDocumentController {

	@Autowired
	private ObjUserCache userCache;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
		ObjUserFM user = (ObjUserFM) this.userCache.get(id);
		Integer documentId = user.getAvatarImageId();
		if (documentId == null) {
			return ResponseEntity.noContent().build();
		}
		return this.documentController.getContent(documentId);
	}

}
