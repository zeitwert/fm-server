package io.zeitwert.fm.oe.adapter.api.rest;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("userDocumentController")
@RequestMapping("/rest/oe/users")
public class UserDocumentController {

	@Autowired
	SessionContext requestCtx;
	@Autowired
	private ObjUserRepository userRepository;
	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
		ObjUser user = this.userRepository.get(id);
		Object documentId = user.getAvatarImageId();
		if (documentId == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", "/default-user.png");
			return new ResponseEntity<byte[]>(headers, HttpStatus.FOUND);
		}
		return this.documentController.getContent((Integer) documentId);
	}

}
