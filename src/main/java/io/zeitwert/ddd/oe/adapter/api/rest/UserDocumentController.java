
package io.zeitwert.ddd.oe.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.oe.service.api.UserService;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;

import java.util.Map;

@RestController("userDocumentController")
@RequestMapping("/rest/oe/users")
public class UserDocumentController {

	@Autowired
	private UserService userService;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
		Integer documentId = this.userService.getUser(id).getAvatarImageId();
		return this.documentController.getContent(documentId);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Integer>> getStatistics() {
		return ResponseEntity.ok().body(userService.getStatistics());
	}

}
